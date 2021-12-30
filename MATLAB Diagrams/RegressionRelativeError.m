function [errorArray] = RegressionRelativeError(X, Yreal, Ypredict)
%calculate relative error of regression for each month

errorArray = zeros(1, 8);
[numOfVists, a] = size(X);

numOfVisitsMonth = zeros(1,8);

for i=1:numOfVists
    month = X(i,1);
    numOfVisitsMonth(1,month) = numOfVisitsMonth(1,month) + 1;
end

for j=1:numOfVists
   month = X(j,1);
   error = abs(Yreal(j,1) - Ypredict(j,1)) / Yreal(j,1);
   errorArray(1, month) =  errorArray (1, month) + error;
end

for k=1:8
    errorArray(1,k) = errorArray(1,k) / numOfVisitsMonth(1,k);
end

end

