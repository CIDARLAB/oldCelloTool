module and_assign(output out1, input a, b);

  assign out1 = ~(~a | ~b);

endmodule
