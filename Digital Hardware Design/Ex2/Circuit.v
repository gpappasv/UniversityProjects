module TrafficLights (output reg GRN,YLW,RED,
		      input wire Clock,Reset,CAR);

reg [1:0] currentState,nextState;

localparam [1:0]//state encoding
	GREEN=2'b00,
	YELLOW=2'b01,
	REDD=2'b10;
localparam [31:0] SEC15=32'd15000;//to count 15 seconds 
localparam [31:0] SEC3=32'd3000;//to count 3 seconds
reg [31:0] count;
reg TIMEOUT,TIMEOUTYLW;

initial
	begin
		count=SEC3;
		TIMEOUT=0;
		TIMEOUTYLW=0;	
	end
	

always@(posedge Clock or posedge Reset)
	begin: STATE_MEMORY
		if(Reset) begin currentState<=GREEN;
				count<=SEC15;//needs to be reset when we leave RED state
			  end
			else currentState<=nextState;
	end

always@(currentState or CAR or posedge TIMEOUT or posedge TIMEOUTYLW)//"calculates" the next state of FSM 
	begin: NEXT_STATE_LOGIC
		case(currentState)
			GREEN: if(CAR) nextState=YELLOW;//if GREEN and CAR -> nextState = YELLOW
				else nextState=GREEN;//if GREEN and the other road is empty -> stay on GREEN (nextState=GREEN)
			YELLOW: if(TIMEOUTYLW)begin nextState=REDD;//When YELLOW, stay on YELLOW for some time (TIMEOUTYLW=3sec) and after that time passes, turn RED on (nextState=REDD)
						
					   end
				else nextState=YELLOW;//if !TIMEOUTYLW -> stay on YELLOW
			REDD:if(TIMEOUT)begin nextState=GREEN;//When REDD, stay on REDD for some time (TIMEOUT=15sec) and after that time passes, turn GREEN on (nextState=GREEN)
						  
					    end	
			    else nextState=REDD;
		default: nextState=GREEN;
		endcase
	end

always@(currentState)
	begin: OUTPUT_LOGIC//depending on the state the FSM is in (currentState), "calculate" the output of the FSM => (turns green, yellow or red on)
	case(currentState)
		GREEN:	begin{GRN,YLW,RED}=3'b100;
			TIMEOUT=0; //reset TIMEOUT to 0 
			end
		YELLOW: begin{GRN,YLW,RED}=3'b010;
			count=SEC3;//count=3seconds -> it serves as timer to stay on a state until it reaches zero.
			end
		REDD: begin{GRN,YLW,RED}=3'b001;
			 count=SEC15;//count=15seconds
			 TIMEOUTYLW=0; //reset TIMEOUTYLW to 0
			 end
	default:{GRN,YLW,RED}=3'b100;
	endcase 
	end

always@(posedge Clock)//Timer block
	begin: COUNTER
		case(currentState)
			REDD:begin//if currentState=REDD, count starts from value stored at SEC15 until it reaches zero -> we end up counting 15 seconds
				count=count-1;//count is decremented on every posedge clock
				if(count==0)TIMEOUT=1'b1;//TIMEOUT -> 1 if count reaches zero (after staying 15 seconds on REDD, TIMEOUT signal is produced so FSM transitions from RED to GREEN
			      end
			YELLOW:begin
				count=count-1;
				if(count==0)TIMEOUTYLW=1'b1;//TIMEOUT -> 1 if counter reaches zero
			      end
		endcase
	end		

endmodule