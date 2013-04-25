# Plot of direct and indirect MoAs. The point here is that 
# when direct and indirect MoA are considered, the range of MoA
# spans much more
# The higher the number of MoA, the more specific a drug is

data_direct <- read.csv(file="/home/samuel/git/ftc/data/analysis/direct-distribution-moas.csv", 
                        head=TRUE, 
                        sep=",", 
                        quote = "\'")

number_direct <- data_direct$numberOfOccurences

#Drugs with 2 or less MoA are not considered, they just don't belong to any categories
# Here just to check how many it is
number_moa <- number_direct[number_direct <= 2]
length(number_moa)

# Remove the compounds not assigned to a FTC category
trim_number_direct <- number_direct[number_direct > 2]
summary(trim_number_direct)

hist(trim_number_direct, 
     breaks=c(1:max(trim_number_direct)), 
     ylab="Number of DrugBank compounds", 
     xlab="Number of mode of actions", 
     main="Distribution of the number \nof mode of actions per approved compounds",
     xlim=c(0,300),
     ylim=c(0,130),
     col=rgb(0,0,1,1/4),
     border = rgb(0,0,1))
# Plot the mean
abline(v = mean(trim_number_direct), col = rgb(0,0,1), lwd = 1)

data_indirect <- read.csv(file="/home/samuel/git/ftc/data/analysis/indirect-distribution-moas.csv", 
                          head=TRUE, 
                          sep=",", 
                          quote = "\'")

no_indirect <- data_indirect$numberOfOccurences
trim_no_indirect <- no_indirect[no_indirect > 4]
summary(trim_no_indirect)
hist(trim_no_indirect, breaks=c(1:max(trim_no_indirect)), 
     xlim=c(0,300),
     ylim=c(0,130),
     add=T,
     col=rgb(1,0,0,1/4),
     border = rgb(1,0,0))

abline(v = mean(trim_no_indirect), col = rgb(1,0,0), lwd = 1)

legend('topright',c('direct','indirect'),
       fill = c(rgb(0,0,1), rgb(1,0,0)), 
       bty = 'n',
       border = NA)
