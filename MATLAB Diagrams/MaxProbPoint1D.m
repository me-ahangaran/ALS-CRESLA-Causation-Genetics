function [ avgVal, avgProb, avgDens ] = MaxProbPoint1D(f, xi, p)
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here

numOfMaxPoints = 10; %numbe of max points for average

prob_length = length(p);
dens_length = length(f);

maxPointsList = zeros(1,numOfMaxPoints);    %list of max points for density list
maxPointsListProb = zeros(1,numOfMaxPoints);    %list of max points for probability list

for i=1:numOfMaxPoints
    d=0;
    idx=1;
    for j=1:dens_length
        if ismember(j, maxPointsList) == 1
            continue;
        end
        if f(j) > d
            idx = j;
            d = f(j);
        end
    end
    maxPointsList(i) = idx;
end

sum = 0;
for i=1:numOfMaxPoints
    sum = sum + xi(maxPointsList(i));
end
avgVal = sum ./ numOfMaxPoints;

sumDens = 0;
for i=1:numOfMaxPoints
    sumDens = sumDens + f(maxPointsList(i));
end
avgDens = sumDens ./ numOfMaxPoints;

%calculate average of probability values
for i=1:numOfMaxPoints
    probVal=0;
    idxProb=1;
    for j=1:prob_length
        if ismember(j, maxPointsListProb) == 1
            continue;
        end
        if p(j) > probVal
            idxProb = j;
            probVal = p(j);
        end
    end
    maxPointsListProb(i) = idxProb;
end

sumProb = 0;
for i=1:numOfMaxPoints
    sumProb = sumProb + p(maxPointsListProb(i));
end
avgProb = sumProb ./ numOfMaxPoints;

end

