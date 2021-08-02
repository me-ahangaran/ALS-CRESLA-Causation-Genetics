function [row, col, prob, dens] = MaxProbPoint2D(probability, density)
%MAXPROBPOINT Summary of this function goes here
%   Detailed explanation goes here
p = 0;
[r, c] = size(probability);

[row, col] = size(density);
d = 0;
rowIdxDens = 1;
colIdxDens = 1;
for k=1:row
    for t=1:col
        if density(k,t) > d
            rowIdxDens = k;
            colIdxDens = t;
            d = density(k,t);
        end
    end
end

for i=1:r
    for j=1:c
        if probability(i,j) > p
            p = probability(i,j);
        end
    end
end

dens = d;
prob = p;
row = rowIdxDens;
col = colIdxDens;
end

