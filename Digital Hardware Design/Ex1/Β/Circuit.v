
module fsml_behavioralB (output reg Dout,
            input wire Clock, Reset, Din);
localparam[2:0] //3bit
    start = 001, //001
    midway = 010, //010
    done = 100; //100
    reg[2:0] currentState, nextState;
 
always @(posedge Clock or posedge Reset)//currentState gets updated with its next value (transition from one state to another)
begin: STATE_MEMORY
    if(Reset) currentState <=start;
    else currentState<=nextState;
end

always @(currentState or Din)//Block where nextState gets its new value (to be given to currentState)
begin: NEXT_STATE_LOGIC
    case(currentState)
        start: if(Din) nextState=midway;
               else nextState = start;
        midway: nextState=done;
        done: nextState=start;
    default: nextState=start;
    endcase
end

always@(currentState or Din)//Block where the output changes
	begin: OUTPUT_LOGIC
	case (currentState)
		done:if(Din==1'b1)
			Dout=1'b1;
		     else
			Dout=1'b0;
	default: Dout=1'b0;
	endcase
end

endmodule