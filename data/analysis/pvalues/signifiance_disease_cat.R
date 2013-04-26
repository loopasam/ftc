same <- read.csv("/home/samuel/git/ftc/data/analysis/pvalues/pval_V_same.csv", 
                 header=FALSE, 
                 sep=","
)

diff <- read.csv("/home/samuel/git/ftc/data/analysis/pvalues/pval_V_diff.csv", 
                 header=FALSE, 
                 sep=","
)

same <- as.matrix(same);
mean(same)
diff <- as.matrix(diff);
mean(diff)
mean(same) - mean(diff)
library(DAAG)
twot.permutation(x1=diff, x2=same, nsim=10000, plotit=TRUE)