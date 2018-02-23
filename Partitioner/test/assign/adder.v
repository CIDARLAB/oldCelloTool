/* 
Full Adder Module for bit Addition 
Written by referencedesigner.com 
*/
module fulladder
(
 input x,
 input y,
 input cin,
 
 output A, 
 output cout
 );
 
assign {cout,A} =  cin + y + x;
 
endmodule
