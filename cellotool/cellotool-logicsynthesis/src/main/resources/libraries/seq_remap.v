module \$_DLATCH_P_ (D, E, Q);
   input D, E;
   output wire Q;

   DLATCH dlatch (
		  .D(D),
		  .E(E),
		  .Q(Q),
		  );
   	
endmodule

module \$_DFF_P_ (C, D, Q);
   input C, D;
   output wire Q;

   DFF dff (
	   .C(C),
	   .D(D),
	   .Q(Q),
	  );
   	
endmodule

module \$_NOR_ (A, B, Y);
   input A, B;
   output wire Y;

   NOR n1 (
	   .A(A),
	   .B(B),
	   .Y(Y),
	  );
   	
endmodule

module \$_NOT_ (A, Y);
   input A;
   output wire Y;

   NOT n1 (
	   .A(A),
	   .Y(Y),
	   );
   
endmodule

module _SR_PP_A_ (S, R, Q, P);
   input S, R;
   output wire Q, P;

   SR sr (
	  .S(S),
	  .R(R),
	  .Q(Q),
	  .P(P),
	  );
   	
endmodule

module _DLATCH_PP_A_ (D, E, Q, P);
   input D, E;
   output wire Q, P;

   DLATCH dlatch (
		  .D(D),
		  .E(E),
		  .Q(Q),
		  .P(P),
	      );
   	
endmodule

module _DLATCH_NN_A_ (D, E, Q, P);
   input D, E;
   wire  w1,w2;
   output wire Q, P;

   NOT n1 (
	   .A(D),
	   .Y(w1),
	   );
   
   NOT n2 (
	   .A(E),
	   .Y(w2),
	   );
   
   DLATCH dlatch (
		  .D(w1),
		  .E(w2),
		  .Q(Q),
		  .P(P),
		  );
   	
endmodule

module _DLATCH_PN_A_ (D, E, Q, P);
   input D, E;
   wire  w1;
   output wire Q, P;

   NOT n1 (
	   .A(E),
	   .Y(w1),
	   );
   
   DLATCH dlatch (
		  .D(D),
		  .E(w1),
		  .Q(Q),
		  .P(P),
		  );
   
endmodule

module _DLATCH_NP_A_ (D, E, Q, P);
   input D, E;
   wire  w1;
   output wire Q, P;

   NOT n1 (
	   .A(D),
	   .Y(w1),
	   );
   
   DLATCH dlatch (
		  .D(w1),
		  .E(E),
		  .Q(Q),
		  .P(P),
	      );
	
endmodule
