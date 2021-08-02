clc
close all
clear all

% ALSFRS slopes for all patients
slopesFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\SupplementaryFiles\Slopes.csv');
slopesData = csvread(slopesFilePath);
[f_slopes,xi_slopes,bwSlopes] = ksdensity(slopesData,'npoints',100,'function','pdf');
figure('Name','Slopes Estimation');
plot(xi_slopes,f_slopes,'LineWidth',1.5);
xlabel('Slope');
ylabel('Density value');
hold on

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% ALSFRS slopes for tardbp
slopesTardbpFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\SupplementaryFiles\ALSFRSSlope-tardbp.csv');
slopesTardbpData = csvread(slopesTardbpFilePath);
[f_slopesTardbp,xi_slopesTardbp,bwSlopesTardbp] = ksdensity(slopesTardbpData,'npoints',100,'function','pdf');
plot(xi_slopesTardbp,f_slopesTardbp,'LineWidth',1.5);
hold on

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% ALSFRS slopes for fus
slopesFusFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\SupplementaryFiles\ALSFRSSlope-fus.csv');
slopesFusData = csvread(slopesFusFilePath);
[f_slopesFus,xi_slopesFus,bwSlopesTardbp] = ksdensity(slopesFusData,'npoints',100,'function','pdf');
plot(xi_slopesFus,f_slopesFus,'LineWidth',1.5);
hold on

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% ALSFRS slopes for sod1
slopesSod1FilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\SupplementaryFiles\ALSFRSSlope-sod1.csv');
slopesSod1Data = csvread(slopesSod1FilePath);
[f_slopesSod1,xi_slopesSod1,bwSlopesSod1] = ksdensity(slopesSod1Data,'npoints',100,'function','pdf');
plot(xi_slopesSod1,f_slopesSod1,'LineWidth',1.5);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% ALSFRS slopes for c9orf72
slopesC9orf72FilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\SupplementaryFiles\ALSFRSSlope-c9orf72.csv');
slopesC9orf72Data = csvread(slopesC9orf72FilePath);
[f_slopesC9orf72,xi_slopesC9orf72,bwSlopesC9orf72] = ksdensity(slopesC9orf72Data,'npoints',100,'function','pdf');
plot(xi_slopesC9orf72,f_slopesC9orf72,'LineWidth',1.5);

legend('All patients', 'TARDBP', 'FUS', 'SOD1', 'C9ORF72')
