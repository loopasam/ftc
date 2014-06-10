same <- read.csv("/home/loopasam/git/ftc/data/analysis/pvalues/pval_A_same.csv", 
                 header=FALSE, 
                 sep=","
)

diff <- read.csv("/home/loopasam/git/ftc/data/analysis/pvalues/pval_A_diff.csv", 
                 header=FALSE, 
                 sep=","
)

same <- as.matrix(same);
mean(same)
diff <- as.matrix(diff);
mean(diff)
mean(same) - mean(diff)
library(DAAG)
twot.permutation(x1=diff, x2=same, nsim=1000000, plotit=TRUE)

densitydiff <- density(diff)
plot( densitydiff, col=rgb(0,0,1,1/4), xlim=c(0,1), lwd=8)
abline(v=mean(diff), col=rgb(0,0,1,1/4), lwd=2)
densitysame <- density(same)
par(new=T)
plot( densitysame, col=rgb(1,0,0,1/4), xlim=c(0,1), lwd=8)  # second
abline(v=mean(diff), col=rgb(1,0,0,1/4), lwd=2)
