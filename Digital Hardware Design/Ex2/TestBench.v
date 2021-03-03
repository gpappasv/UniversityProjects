`timescale 100us/10us
module ATB ();
    reg clk;
    reg reset;
    reg in;
 	
    wire g,y,r;
      
    TrafficLights dut(g, y, r, clk,reset,in);
       
    initial
	begin
		clk=1'b1;
		in=0;
		reset=0;
	end
initial
	begin
		 in=1;
		#15in=0;
		#150in=1;
		#25000in=0;
		#25050in=1;
		#30000in=0;	
	end
 
always
    begin
        #5 clk= ~clk;
    end
endmodule