clc
close all
clear all

%slopes for genetic patients and all patients
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Genetic patients
% slopesGeneticFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\SupplementaryFiles\GeneticSlopes.csv');
% slopesGeneticData = csvread(slopesGeneticFilePath);
% [f_slopesGenetic,xi_slopesGenetic,bwSlopesGenetic] = ksdensity(slopesGeneticData,'npoints',100,'function','pdf');
% figure('Name','Slopes Estimation');
% % plot(xi_slopesFast,f_slopesFast,'LineWidth',1.5);
% fill(xi_slopesGenetic,f_slopesGenetic,'y');
% xlabel('Slope');
% ylabel('Density');
% % alpha(.5);
% 
% hold on
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% C9ORF72 patients
slopesC9ORF72FilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\SupplementaryFiles\ALSFRSSlope-c9orf72.csv');
slopesC9ORF72Data = csvread(slopesC9ORF72FilePath);
[f_slopesC9ORF72,xi_slopesC9ORF72,bwSlopesC9ORF72] = ksdensity(slopesC9ORF72Data,'npoints',100,'function','pdf');
% plot(xi_slopesC9ORF72,f_slopesC9ORF72, 'r', 'LineWidth',2);
fill(xi_slopesC9ORF72,f_slopesC9ORF72,'r');
xlabel('Slope');
ylabel('Density');
alpha(.5);

hold on
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% SOD1 patients
slopesSOD1FilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\SupplementaryFiles\ALSFRSSlope-sod1.csv');
slopesSOD1Data = csvread(slopesSOD1FilePath);
[f_slopesSOD1,xi_slopesSOD1,bwSlopesSOD1] = ksdensity(slopesSOD1Data,'npoints',100,'function','pdf');
% plot(xi_slopesSOD1,f_slopesSOD1,'b','LineWidth',2);
fill(xi_slopesSOD1,f_slopesSOD1,'g');
xlabel('Slope');
ylabel('Density');
alpha(.5);

hold on
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% TARDBP patients
slopesTARDBPFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\SupplementaryFiles\ALSFRSSlope-tardbp.csv');
slopesTARDBPData = csvread(slopesTARDBPFilePath);
[f_slopesTARDBP,xi_slopesTARDBP,bwSlopesTARDBP] = ksdensity(slopesTARDBPData,'npoints',100,'function','pdf');
% plot(xi_slopesTARDBP,f_slopesTARDBP,'g','LineWidth',2);
fill(xi_slopesTARDBP,f_slopesTARDBP,'b');
xlabel('Slope');
ylabel('Density');
alpha(.5);

hold on
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% All patients
% slopesFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\SupplementaryFiles\Slopes.csv');
% slopesData = csvread(slopesFilePath);
% [f_slopes,xi_slopes,bwSlopes] = ksdensity(slopesData,'npoints',100,'function','pdf');
% % plot(xi_slopes,f_slopes,'LineWidth',1.5);
% fill(xi_slopes,f_slopes,'m');
% alpha(.5);

legend('C9ORF72 patients', 'SOD1 patients', 'TARDBP patients')

