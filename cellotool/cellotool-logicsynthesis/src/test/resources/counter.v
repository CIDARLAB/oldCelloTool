module counter(clk, a, b);
   input clk;
   output reg a,b;

   initial begin
      a = 0;
      b = 0;
   end
   
   always @ (posedge clk)
     begin
	a <= ~a;
	b <= a ^ b;
     end
endmodule // counter

