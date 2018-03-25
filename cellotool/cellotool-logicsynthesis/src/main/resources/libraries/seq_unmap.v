(* extract_order = 1 *)
module _DLATCH_NN_A_ (D, E, Q, P);
   input D, E;
   wire  w1,w2;
   output wire Q, P;

   $_NOR_ n1 (
	      .A(Qn),
	      .B(w1),
	      .Y(Q),
	      );
   
   $_NOR_ n2 (
	      .A(w2),
	      .B(Q),
	      .Y(P),
	      );

   $_NOR_ n3 (
	      .A(w3),
	      .B(E),
	      .Y(w1),
	      );
   
   $_NOT_ n4 (
	      .A(E),
	      .Y(w2),
	      );

   $_NOT_ n5 (
	      .A(D),
	      .Y(w3),
	      );
	
endmodule

(* extract_order = 10 *)
module _DLATCH_PN_A_ (D, E, Q, P);
   input D, E;
   wire  w1,w2;
   output wire Q, P;

   $_NOR_ n1 (
	      .A(P),
	      .B(w1),
	      .Y(Q),
	      );
   
   $_NOR_ n2 (
	      .A(w2),
	      .B(Q),
	      .Y(P),
	      );

   $_NOR_ n3 (
	      .A(E),
	      .B(D),
	      .Y(w1),
	      );
   
   $_NOT_ n4 (
	      .A(E),
	      .Y(w2),
	      );

endmodule

(* extract_order = 10 *)
module _DLATCH_NP_A_ (D, E, Q, P);
   input D, E;
   wire  w1,w2;
   output wire Q, P;

   $_NOR_ n1 (
	      .A(P),
	      .B(w1),
	      .Y(Q),
	      );
   
   $_NOR_ n2 (
	      .A(E),
	      .B(Q),
	      .Y(P),
	      );

   $_NOR_ n3 (
	      .A(w3),
	      .B(w2),
	      .Y(w1),
	      );
   
   $_NOT_ n4 (
	      .A(E),
	      .Y(w2),
	      );

   $_NOT_ n5 (
	      .A(D),
	      .Y(w3),
	      );
	
endmodule

(* extract_order = 20 *)
module _DLATCH_PP_A_ (D, E, Q, P);
   input D, E;
   wire  w1,w2;
   output wire Q, P;

   $_NOR_ n1 (
	      .A(Qn),
	      .B(w1),
	      .Y(Q),
	      );
   
   $_NOR_ n2 (
	      .A(E),
	      .B(Q),
	      .Y(P),
	      );

   $_NOR_ n3 (
	      .A(D),
	      .B(w2),
	      .Y(w1),
	      );
   
   $_NOT_ n4 (
	      .A(E),
	      .Y(w2),
	      );
	
endmodule

(* extract_order = 100 *)
module _SR_PP_A_ (S, R, Q, P);
   input S, R;
   output wire Q, P;

   $_NOR_ n1 (
	      .A(S),
	      .B(Q),
	      .Y(P),
	      );
   
   $_NOR_ n2 (
	      .A(P),
	      .B(R),
	      .Y(Q),
	      );

endmodule
