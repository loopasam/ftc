setwd("~/git/ftc/data/analysis")
data_direct <- read.csv(file="direct-distribution-moas.csv", head=TRUE, sep=",", quote = "\'")
no_direct <- data_direct$numberOfOccurences
no_moa <- no_direct[no_direct <= 2]
length(no_moa)
trim_no_direct <- no_direct[no_direct > 2]
summary(trim_no_direct)
hist(trim_no_direct, breaks=c(1:max(trim_no_direct)), ylab="Frequency", xlab="# of direct MoAs", main="Direct MoA distribution")
abline(v = mean(trim_no_direct), col = "red", lwd = 1)
abline(v = median(trim_no_direct), col = "blue", lwd = 1)

data_indirect <- read.csv(file="indirect-distribution-moas.csv", head=TRUE, sep=",", quote = "\'")
no_indirect <- data_indirect$numberOfOccurences
trim_no_indirect <- no_indirect[no_indirect > 4]
summary(trim_no_indirect)
hist(trim_no_indirect, breaks=c(1:max(trim_no_indirect)), ylab="Frequency", xlab="# of indirect MoAs", main="Indirect MoA distribution")
abline(v = mean(trim_no_indirect), col = "red", lwd = 1)
abline(v = median(trim_no_indirect), col = "blue", lwd = 1)