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
     xlab="Number of direct mode of actions", 
     main="Distribution of the number \nof direct mode of actions per approved compounds",
     col=rgb(0,0,1,1/4),
     border = rgb(0,0,1))

# Plot the mean
abline(v = mean(trim_number_direct), col = rgb(0,0,1), lwd = 1)