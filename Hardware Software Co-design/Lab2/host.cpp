#include "xcl2.hpp"
#include <algorithm>
#include <vector>
#include <ap_int.h>

#define lm 8
#define ln 8
#define lp 8

#define m (1<<lm)
#define n (1<<ln)
#define p (1<<lp)

int main(int argc, char **argv) {
  if (argc != 2) {
    std::cout << "Usage: " << argv[0] << " <XCLBIN File>" << std::endl;
    return EXIT_FAILURE;
  }

  std::string binaryFile = argv[1];

  size_t vector_size_bytes1 = sizeof(ap_int<32>) * n*m;//arrayA
  size_t vector_size_bytes2 = sizeof(ap_int<32>) * m*p;//arrayB
  size_t vector_size_bytes3 = sizeof(ap_int<32>) * n*p;//arrayAB

  cl_int err;
  cl::Context context;
  cl::Kernel krnl_matrix_mul;//cl::Kernel krnl_matrix_mul; OLDDDDDDDDDDD
  cl::CommandQueue q;
  // Allocate Memory in Host Memory
  // When creating a buffer with user pointer (CL_MEM_USE_HOST_PTR), under the
  // hood user ptr
  // is used if it is properly aligned. when not aligned, runtime had no choice
  // but to create
  // its own host side buffer. So it is recommended to use this allocator if
  // user wish to
  // create buffer using CL_MEM_USE_HOST_PTR to align user buffer to page
  // boundary. It will
  // ensure that user buffer is used when user create Buffer/Mem object with
  // CL_MEM_USE_HOST_PTR
  std::vector<int, aligned_allocator<int>> source_in1(n*m);//std::vector<int, aligned_allocator<int>> source_in1(DATA_SIZE);
  std::vector<int, aligned_allocator<int>> source_in2(m*p);
  std::vector<int, aligned_allocator<int>> source_hw_results(n*p);
  std::vector<int, aligned_allocator<int>> source_sw_results(n*p);

  // Create the test data
  std::generate(source_in1.begin(), source_in1.end(), std::rand);
  std::generate(source_in2.begin(), source_in2.end(), std::rand);


for(int i = 0; i < n; i++){

    for(int j = 0; j < p; j++){

        int result = 0;
        for(int k = 0; k < m; k++){
            result += source_in1[i*m+k]*source_in2[k*p+j];

        }
        source_sw_results[i*p+j] = result;
        source_hw_results[i*p+j]=0;
    }
}


  // OPENCL HOST CODE AREA START
  // get_xil_devices() is a utility API which will find the xilinx
  // platforms and will return list of devices connected to Xilinx platform
  auto devices = xcl::get_xil_devices();
  // read_binary_file() is a utility API which will load the binaryFile
  // and will return the pointer to file buffer.
  auto fileBuf = xcl::read_binary_file(binaryFile);
  cl::Program::Binaries bins{{fileBuf.data(), fileBuf.size()}};
  int valid_device = 0;
  for (unsigned int i = 0; i < devices.size(); i++) {
    auto device = devices[i];
    // Creating Context and Command Queue for selected Device
    OCL_CHECK(err, context = cl::Context(device, NULL, NULL, NULL, &err));
    OCL_CHECK(err, q = cl::CommandQueue(context, device,
                                        CL_QUEUE_PROFILING_ENABLE, &err));
    std::cout << "Trying to program device[" << i
              << "]: " << device.getInfo<CL_DEVICE_NAME>() << std::endl;
    cl::Program program(context, {device}, bins, NULL, &err);
    if (err != CL_SUCCESS) {
      std::cout << "Failed to program device[" << i << "] with xclbin file!\n";
    } else {
      std::cout << "Device[" << i << "]: program successful!\n";
      OCL_CHECK(err, krnl_matrix_mul = cl::Kernel(program, "vadd", &err));
      valid_device++;
      break; // we break because we found a valid device
    }
  }
  if (valid_device == 0) {
    std::cout << "Failed to program any device found, exit!\n";
    exit(EXIT_FAILURE);
  }

  // Allocate Buffer in Global Memory
  // Buffers are allocated using CL_MEM_USE_HOST_PTR for efficient memory and
  // Device-to-host communication
  OCL_CHECK(err, cl::Buffer buffer_in1(
                     context, CL_MEM_USE_HOST_PTR | CL_MEM_READ_ONLY,
                     vector_size_bytes1, source_in1.data(), &err));
  OCL_CHECK(err, cl::Buffer buffer_in2(
                     context, CL_MEM_USE_HOST_PTR | CL_MEM_READ_ONLY,
                     vector_size_bytes2, source_in2.data(), &err));
  OCL_CHECK(err, cl::Buffer buffer_output(
                     context, CL_MEM_USE_HOST_PTR | CL_MEM_WRITE_ONLY,
                     vector_size_bytes3, source_hw_results.data(), &err));


  OCL_CHECK(err, err = krnl_matrix_mul.setArg(0, buffer_in1));
  OCL_CHECK(err, err = krnl_matrix_mul.setArg(1, buffer_in2));
  OCL_CHECK(err, err = krnl_matrix_mul.setArg(2, buffer_output));
  //OCL_CHECK(err, err = krnl_matrix_mul.setArg(3, size));

  // Copy input data to device global memory
  OCL_CHECK(err, err = q.enqueueMigrateMemObjects({buffer_in1, buffer_in2},
                                                  0 /* 0 means from host*/));

  // Launch the Kernel
  // For HLS kernels global and local size is always (1,1,1). So, it is
  // recommended
  // to always use enqueueTask() for invoking HLS kernel
  OCL_CHECK(err, err = q.enqueueTask(krnl_matrix_mul));

  // Copy Result from Device Global Memory to Host Local Memory
  OCL_CHECK(err, err = q.enqueueMigrateMemObjects({buffer_output},
                                                  CL_MIGRATE_MEM_OBJECT_HOST));
  q.finish();
  // OPENCL HOST CODE AREA END

  // Compare the results of the Device to the simulation
  bool match = true;
/*
std::cout << "SW resulttttttttt: ";
std::cout << "\n";
for(int i = 0; i < n; i++ ){
    for(int j = 0; j < p; j++){
        std::cout << source_sw_results[i*p+j];
        std::cout << "\t";
    }std::cout << "\n";}

std::cout << "HW resultttttttttttt: ";
std::cout << "\n";
for(int i = 0; i < n; i++ ){
    for(int j = 0; j < p; j++){
        std::cout << source_hw_results[i*p+j];
        std::cout << "\t";
    }std::cout << "\n";}
*/

for(int i = 0; i < n; i++ )
    for(int j = 0; j < p; j++){
        if(source_sw_results[i*p+j] !=source_hw_results[i*p+j]){
            std::cout << "ERROR: ["<<i<<"]["<<j<<"]\n";
            match = false;

        }
    }
if(match == true)std::cout <<"TEST PASSED\n";
else std::cout <<"TEST FAILED\n";
return (match ? EXIT_SUCCESS : EXIT_FAILURE);
}
