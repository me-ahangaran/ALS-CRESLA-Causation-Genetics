clc
clear all
close all

cfdIdx = csvread('E:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\CFDMatrix\CFDMatIdxCresla.csv');

[row, col] = size(cfdIdx);
bw = 80;

X_Mat = zeros(row,bw);
Y_Mat = zeros(row,bw);
Xp_Mat = zeros(row,bw-1);
Yp_Mat = zeros(row,bw-1);
entropyProb = zeros(row,1);
P_Mat = zeros(row,bw-1);    %probability values
F_Mat = zeros(row,bw);
maxProb_Mat = zeros(row,3); %max point probability. (FrsChange, ProbValue, DensValue)

%initial density and prob entropy list
for i=1:row
    entropyProb(i,1) = -1;
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%calculate max points of causal links with maximum density
for idx=1:row
    cause = cfdIdx(idx,1);
    effect = cfdIdx(idx,2);
    strCause = num2str(cause);
    strEffect = num2str(effect);
    FrsAbsFilePath = strcat('E:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\CFDMatrix\Ages\',strCause,'-',strEffect,'.csv');
    FrsAbsData = csvread(FrsAbsFilePath);
    
    if mod(idx,10) == 0
        disp(idx);
    end
    try
        [f,xi,bandwidth] = ksdensity(FrsAbsData,'npoints',bw,'function','pdf');
        X_Mat(idx,:) = xi(1,:);
        F_Mat(idx,:) = f(1,:);
        p = zeros(1, bw-1);
        xp = zeros(1, bw-1);        
        for i1=1:(bw-2)
            xp(1,i1) = (xi(1,i1) + xi(1,i1+1)) / 2;
            p(1,i1) = ((f(1,i1) + f(1,i1+1)) / 2) * (xi(1,i1+1) - xi(1,i1));
        end
        [maxVal, probVal, densVal] = MaxProbPoint1D(f,xi,p);
        maxProb_Mat(idx,1) = maxVal;
        maxProb_Mat(idx,2) = probVal;
        maxProb_Mat(idx,3) = densVal;
        entProb = Entropy(p);        
        entropyProb(idx,1) = entProb;
        X_Mat(idx,:) = xi(1,:);
        P_Mat(idx,:) = p(1,:);
        Xp_Mat(idx,:) = xp(1,:);
                
    catch exception
        if strcmp(exception.identifier,'MATLAB:fzero:ValuesAtEndPtsComplexOrNotFinite')...
                || strcmp(exception.identifier,'MATLAB:fzero:ValuesAtEndPtsSameSign')
            continue;
        end
    end
end


fnameMaxProb = strcat('E:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\CFDMatrix\MaxPointsAgesProbDens.csv');
fid_MaxProb = fopen(fnameMaxProb,'w');
dlmwrite(fnameMaxProb,maxProb_Mat);
fclose(fid_MaxProb); 

fnameEntropyAbs = strcat('E:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\CFDMatrix\EntropyAges.csv');
fid_EntAbs = fopen(fnameEntropyAbs,'w');
dlmwrite(fnameEntropyAbs,entropyProb);
fclose(fid_EntAbs);
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


