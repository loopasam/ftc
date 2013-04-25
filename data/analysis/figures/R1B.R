# Distribution of MoA for the DrugBank compounds
# the indirect MoA are considered too

data_indirect <- read.csv(file="/home/samuel/git/ftc/data/analysis/indirect-distribution-moas.csv", 
                          head=TRUE, 
                          sep=",", 
                          quote = "\'")

no_indirect <- data_indirect$numberOfOccurences
# Keeps only the compounds with more than 4 reported moas
trim_no_indirect <- no_indirect[no_indirect > 4]
summary(trim_no_indirect)
hist(trim_no_indirect, breaks=c(1:max(trim_no_indirect)), 
     ylab="Number of DrugBank compounds", 
     xlab="Number of indirect mode of actions", 
     main="Distribution of the number \nof indirect mode of actions per approved compounds",
     col=rgb(1,0,0,1/4),
     border = rgb(1,0,0))

abline(v = mean(trim_no_indirect), col = rgb(1,0,0), lwd = 1)
