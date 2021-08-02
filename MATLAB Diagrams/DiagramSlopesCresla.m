clc
close all
clear all

%slopes for fast, intermediate, slow patients
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Fast patients
slopesFastFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\SupplementaryFiles\SlopesFast.csv');
slopesFastData = csvread(slopesFastFilePath);
[f_slopesFast,xi_slopesFast,bwSlopesFast] = ksdensity(slopesFastData,'npoints',100,'function','pdf');
figure('Name','Slopes Estimation');
% plot(xi_slopesFast,f_slopesFast,'LineWidth',1.5);
fill(xi_slopesFast,f_slopesFast,'b');
xlabel('Slope');
ylabel('Density');
alpha(.5);

hold on
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Intermediate patients
slopesIntermediateFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\SupplementaryFiles\SlopesMedium.csv');
slopesIntermediateData = csvread(slopesIntermediateFilePath);
[f_slopesIntermediate,xi_slopesIntermediate,bwSlopesIntermediate] = ksdensity(slopesIntermediateData,'npoints',100,'function','pdf');
% plot(xi_slopesIntermediate,f_slopesIntermediate,'LineWidth',1.5);
fill(xi_slopesIntermediate,f_slopesIntermediate,'c');
alpha(.5);
hold on
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Slow patients
slopesSlowFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\SupplementaryFiles\SlopesSlow.csv');
slopesSlowData = csvread(slopesSlowFilePath);
[f_slopesSlow,xi_slopesSlow,bwSlopesSlow] = ksdensity(slopesSlowData,'npoints',100,'function','pdf');
% plot(xi_slopesSlow,f_slopesSlow,'LineWidth',1.5);
fill(xi_slopesSlow,f_slopesSlow,'r');
alpha(.5);
hold on
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% All patients
slopesFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\SupplementaryFiles\Slopes.csv');
slopesData = csvread(slopesFilePath);
[f_slopes,xi_slopes,bwSlopes] = ksdensity(slopesData,'npoints',100,'function','pdf');
% plot(xi_slopes,f_slopes,'LineWidth',1.5);
fill(xi_slopes,f_slopes,'m');
alpha(.5);
hold on

legend('Fast', 'Intermediate', 'Slow', 'All patients')

