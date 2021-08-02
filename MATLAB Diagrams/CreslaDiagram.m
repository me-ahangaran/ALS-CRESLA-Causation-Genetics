%This script shows the diagram of selected causal links
clc
close all
clear all

idx = 119;

cfdIdx = csvread('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\CFDMatrix\CFDMatIdxCresla.csv');
cause = cfdIdx(idx,1);    
effect = cfdIdx(idx,2);   

%the name ALSFRS parameters
fileId = fopen('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\FrsParams.txt');
C = textscan(fileId,'%s');
featuresName = C{1,1};
fclose(fileId);

causeName = featuresName(cause);
effectName = featuresName(effect);

strCause = num2str(cause);
strEffect = num2str(effect);

agesFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\CFDMatrix\Ages\',strCause,'-',strEffect,'.csv');
causeEffectChangesFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\CFDMatrix\CauseEffectChanges\',strCause,'-',strEffect,'.csv');
frsTotalAbsChangesFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\CFDMatrix\FrsTotalAbsChanges\',strCause,'-',strEffect,'.csv');
frsTotalRelativeChangesFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\CFDMatrix\FrsTotalRelativeChanges\',strCause,'-',strEffect,'.csv');
timeDistancesFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\CFDMatrix\TimeDistances\',strCause,'-',strEffect,'.csv');
timePositionsFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\CFDMatrix\TimePositions\',strCause,'-',strEffect,'.csv');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%ages diagram
agesData = csvread(agesFilePath);
[f_ages,xi_ages,bwAges] = ksdensity(agesData,'npoints',100,'function','pdf');
figure('Name','Ages Estimation')
plot(xi_ages,f_ages);
xlabel('Ages changes');
ylabel('Density value');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%time distances diagram
timeDistancesData = csvread(timeDistancesFilePath);
[f_timeDistances,xi_timeDistances,bwTimeDistances] = ksdensity(timeDistancesData,'npoints',100,'function','pdf');
figure('Name','Time distances Estimation')
plot(xi_timeDistances,f_timeDistances);
xlabel('Time distances changes');
ylabel('Density value');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%time positions diagram
timePositionsData = csvread(timePositionsFilePath);
[f_timePositionsData,xi_timePositionsData,bwTimePositionsData] = ksdensity(timePositionsData,'npoints',100,'function','pdf');
figure('Name','Time positions Estimation')
plot(xi_timePositionsData,f_timePositionsData);
xlabel('Time positions changes');
ylabel('Density value');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%FRS absolute total values diagram
frsTotalAbsData = csvread(frsTotalAbsChangesFilePath);
[f_frsTotalAbs,xi_frsTotalAbs,bwFrsTotalAbs] = ksdensity(frsTotalAbsData,'npoints',100,'function','pdf');
figure('Name','ALSFRS absolute total Estimation')
plot(xi_frsTotalAbs,f_frsTotalAbs);
xlabel('ALSFRS absolute changes');
ylabel('Density value');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%FRS relative total values diagram
frsTotalRelativeData = csvread(frsTotalRelativeChangesFilePath);
[f_frsTotalRelative,xi_frsTotalRelative,bwFrsTotalRelative] = ksdensity(frsTotalRelativeData,'npoints',100,'function','pdf');
figure('Name','ALSFRS relative total Estimation')
plot(xi_frsTotalRelative,f_frsTotalRelative);
xlabel('ALSFRS relative changes');
ylabel('Density value');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%causal effect diagram
causalLinkData = csvread(causeEffectChangesFilePath);
[bandwidth,density,probability,X,Y,X_p,Y_p]=kde2d(causalLinkData,64);
figure('Name','Estimation of Causal edge')
contour3(X,Y,density,100), hold on
plot(causalLinkData(:,1),causalLinkData(:,2),'r.','MarkerSize',5)
xlabel(strcat({'Cause = '},causeName));
ylabel(strcat({'Effect = '},effectName));
zlabel('Density value');
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

