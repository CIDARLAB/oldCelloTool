module DFF (C, D, Q);
   input C, D;
   output wire Q;
endmodule
module SR (S, R, Q, P);
   input S, R;
   output wire Q, P;
endmodule
module DLATCH (D, E, Q, P);
   input D, E;
   output wire Q, P;
endmodule
module NOT (A, Y);
   input A;
   output wire Y;
endmodule
module NOR (A, B, Y);
   input A, B;
   output wire Y;
endmodule
