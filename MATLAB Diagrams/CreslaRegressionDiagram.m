clc
close all
clear all

% ALSFRS values for all patients (8 months)
regFilePath = strcat('D:\PHD\Thesis\Implementation\ALS-Matlab\Cresla\SupplementaryFiles\ALSFRS-Regression.csv');
regData = csvread(regFilePath);

trainRegData = regData(1:1000,:);
testRegData = regData(900:1100,:);

%calculate number of non-zero elements (number of visits) for train data
numOfVisitsTrain = 0;
for row=1:1000
    for col=1:8
        if(trainRegData(row,col) ~= 0)
            numOfVisitsTrain = numOfVisitsTrain+1;
        end
    end
end

%create X and Y arrays for regression (X = time points, Y = ALSFRS-R values)
Xtrain = zeros(numOfVisitsTrain,1);
Ytrain = zeros(numOfVisitsTrain,1);
ctr = 1;    %current index
for row=1:1000
    for col=1:8
        if(trainRegData(row,col) ~= 0)
            Xtrain(ctr,1) = col;
            Ytrain(ctr,1) = trainRegData(row,col);
            ctr = ctr+1;
        end
    end
end

polyReg = polyfit(Xtrain, Ytrain, 2);  %Polynomial regression
svmReg = fitrsvm(Xtrain, Ytrain);  %SVM regression
linReg = fitlm(Xtrain, Ytrain); %linear regression

%calculate number of non-zero elements (number of visits) for test data
numOfVisitsTest = 0;
for row=1:201
    for col=1:8
        if(testRegData(row,col) ~= 0)
            numOfVisitsTest = numOfVisitsTest+1;
        end
    end
end

%create X and Y arrays for regression (X = time points, Y = ALSFRS-R values)
Xtest = zeros(numOfVisitsTest,1);
Ytest = zeros(numOfVisitsTest,1);
ctr = 1;    %current index
for row=1:201
    for col=1:8
        if(testRegData(row,col) ~= 0)
            Xtest(ctr,1) = col;
            Ytest(ctr,1) = testRegData(row,col);
            ctr = ctr+1;
        end
    end
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Test phase for polynomial regression
YtestPredictPoly = polyval(polyReg, Xtest);
figure
plot(Xtest, Ytest,'o', 'MarkerEdgeColor', 'b');

hold on
plot(Xtest, YtestPredictPoly, 'r', 'Linewidth', 3);
hold on

errorPloyReg = RegressionRelativeError(Xtest,Ytest, YtestPredictPoly);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Average prediction
X = 1:8;
avgALSFRS = [40.162,36.378,34.260,32.778,31.386,30.333,29.150,27.961];
plot(X, avgALSFRS, 'k', 'Linewidth', 3);

YtestPredictAvg = zeros(numOfVisitsTest,1);
for i=1:numOfVisitsTest
    month = Xtest(i,1);
    YtestPredictAvg(i,1) = avgALSFRS(month);
end

errorAvgALSFRS = RegressionRelativeError(Xtest,Ytest, YtestPredictAvg);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Test phase for SVM regression

YtestPredictSVM = predict(svmReg,Xtest);

plot(Xtest, YtestPredictSVM, 'g', 'Linewidth', 3);
hold on

errorSVMReg = RegressionRelativeError(Xtest,Ytest, YtestPredictSVM);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Linear regression
YtestPredictLinear = feval(linReg,Xtest);

plot(Xtest, YtestPredictLinear, 'c', 'Linewidth', 3);
hold on

errorLinReg = RegressionRelativeError(Xtest,Ytest, YtestPredictLinear);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

xlabel('Month');
ylabel('ALSFRS-R');

legend('Individuals', 'Quadratic polynomial regression','Average prediction', 'Support vector regression', 'Linear regression');

