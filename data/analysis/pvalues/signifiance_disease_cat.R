codes <- c("A","B","C", "D", "G", "H", "J", "L", "M", "N", "P", "R", "S", "V");
library(DAAG)
path = "/home/loopasam/git/ftc/data/analysis/pvalues/";

for(code in codes) {
  print(code)
  file <- paste(path,"pval_", code,"_same.csv",sep="")
  same <- read.csv(file, 
                 header=FALSE, 
                 sep=","
  )
  file <- paste(path,"pval_", code,"_diff.csv",sep="")
  diff <- read.csv(file, 
                 header=FALSE, 
                 sep=","
  )

  same <- as.matrix(same);
  # mean(same)
  diff <- as.matrix(diff);
  # mean(diff)
  # mean(same) - mean(diff)
  twot.permutation(x1=diff, x2=same, nsim=1000000, plotit=TRUE)
}

densitydiff <- density(diff)
plot( densitydiff, col=rgb(0,0,1,1/4), xlim=c(0,1), lwd=8)
abline(v=mean(diff), col=rgb(0,0,1,1/4), lwd=2)
densitysame <- density(same)
par(new=T)
plot( densitysame, col=rgb(1,0,0,1/4), xlim=c(0,1), lwd=8)  # second
abline(v=mean(diff), col=rgb(1,0,0,1/4), lwd=2)