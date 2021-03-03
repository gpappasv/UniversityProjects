`timescale 1ns/1ns
module ATB ();
    reg clk;
    reg reset;
    reg in;
 
    wire out;
    
    fsml_behavioral dut(.Dout(out), .Clock(clk), .Reset(reset), .Din(in));
    reg [1:0] testVector[17:0];
   
    initial
     begin
        $readmemb("tbValues.txt", testVector);
        
	{reset, in} = testVector[0];
        #25{reset, in} = testVector[1];
	#5{reset, in} = testVector[2];
	#10{reset, in} = testVector[3];
	#10{reset, in} = testVector[4];
	#10{reset, in} = testVector[5];
	#10{reset, in} = testVector[6];
	#10{reset, in} = testVector[7];
	#10{reset, in} = testVector[8];
	#10{reset, in} = testVector[9];
	#10{reset, in} = testVector[10];
	#10{reset, in} = testVector[11];
	#10{reset, in} = testVector[12];
	#2{reset, in} = testVector[13];
	#2{reset, in} = testVector[14];
	#2{reset, in} = testVector[15];
	#2{reset, in} = testVector[16];
	#2{reset, in} = testVector[17];

    end
    initial
	begin
		clk=1'b1;
	end

always
    begin
        #5 clk= ~clk;
    end
endmodule