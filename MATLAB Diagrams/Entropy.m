function [ out ] = Entropy(probMat)
%ENTROPYDIST Summary of this function goes here
%   Detailed explanation goes here

[row,col] = size(probMat);
sum = 0;
for i=1:row
    for j=1:col
        p = probMat(i,j);
        if p>0
            sum = sum - (p .* log2(p));
        end
    end
end
out = sum;
end

