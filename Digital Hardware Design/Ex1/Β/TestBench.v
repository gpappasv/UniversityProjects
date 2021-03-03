`timescale 1ns/1ns
module ATB ();
    reg clk;
    reg reset;
    reg in;
 
    wire out;
      
    fsml_behavioral dut(.Dout(out), .Clock(clk), .Reset(reset), .Din(in));
    reg [1:0] testVector[9:0];
   
    initial
     begin
        $readmemb("tbValues.txt", testVector);
        
	{reset, in} = testVector[0];
        #25{reset, in} = testVector[1];
	#15{reset, in} = testVector[2];
	#40{reset, in} = testVector[3];
	#20{reset, in} = testVector[4];
	#30{reset, in} = testVector[5];
	#60{reset, in} = testVector[6];
	#15{reset, in} = testVector[7];
	#20{reset, in} = testVector[8];
	#45{reset, in} = testVector[9];

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