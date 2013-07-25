Changes made to tiny_mce.js:

#1) Decrease the RTE's absolute minimum height

This snippet of code:
if(G.test(""+A)){A=Math.max(parseInt(A)+(q.deltaHeight||0),74)}

was originally:
if(G.test(""+A)){A=Math.max(parseInt(A)+(q.deltaHeight||0),100)}