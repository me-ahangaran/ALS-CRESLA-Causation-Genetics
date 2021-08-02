clc
clear all
close all

cfdIdx = csvread('E:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\CFDMatrix\CFDMatIdxCresla.csv');

[row, col] = size(cfdIdx);
bw = 64;

X_Mat = zeros(row,bw);
Y_Mat = zeros(row,bw);
Xp_Mat = zeros(row,bw-1);
Yp_Mat = zeros(row,bw-1);
entropyProb = zeros(row,1);
maxProb_Mat = zeros(row,4); %max point probability. (prevChange, nextChange, probValue, densValue)

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
    causeEffectChangesFilePath = strcat('E:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\CFDMatrix\CauseEffectChanges\',strCause,'-',strEffect,'.csv');
    causalLinkData = csvread(causeEffectChangesFilePath);
    
    if mod(idx,10) == 0
        disp(idx);
    end
    try
        [bandwidth,density,probability,X,Y,X_p,Y_p]=kde2d(causalLinkData,bw);
        entProb = Entropy(probability);        
        entropyProb(idx,1) = entProb;
        X_Mat(idx,:) = X(1,:);
        Y_Mat(idx,:) = Y(:,1)';
        Xp_Mat(idx,:) = X_p;
        Yp_Mat(idx,:) = Y_p';
        
        %max point distribution setting based on probability
        [rowIdx, colIdx, prob, dens] = MaxProbPoint2D(probability, density);
        maxProb_Mat(idx,1) = X(rowIdx, colIdx);   %this is true do not modify it. this is tested with real data
        maxProb_Mat(idx,2) = Y(rowIdx, colIdx);   %this is true do not modify it. this is tested with real data
        maxProb_Mat(idx,3) = prob;
        maxProb_Mat(idx,4) = dens;
      
    catch exception
        if strcmp(exception.identifier,'MATLAB:fzero:ValuesAtEndPtsComplexOrNotFinite')...
                || strcmp(exception.identifier,'MATLAB:fzero:ValuesAtEndPtsSameSign')
            continue;
        end
    end
end

 fnameMaxProb = strcat('E:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\CFDMatrix\MaxPointsCausalLinksProbDens.csv');
 fid_MaxProb = fopen(fnameMaxProb,'w');
 dlmwrite(fnameMaxProb,maxProb_Mat);
 fclose(fid_MaxProb);
 
 fnameEntProb = strcat('E:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\CFDMatrix\EntropyCausalLinks.csv');
 fid_EntProb = fopen(fnameEntProb,'w');
 dlmwrite(fnameEntProb,entropyProb);
 fclose(fid_EntProb);
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


