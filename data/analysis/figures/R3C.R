# Plot of sim(moa) vs. sim(structure)
# Only compounds that are present in different categories (based on a treshold) are
# represented.

values <- read.csv("/home/samuel/git/ftc/data/analysis/same_cats_4lvl.csv", 
                   head=TRUE, 
                   sep=","
)

moa <- values$firstSim
struc <- values$secondSim
color <- '#0003bf'

plot(struc, 
     moa,
     xlim=c(0,1),
     ylim=c(0,1),
     pch='*',
     main='Structural similarity versus mode of action similarity\nOnly compounds present in the same ATC categories (two levels)', 
     ylab="Mode of action similarity (Jaccard index over ancestor classes)",
     xlab="Structural similarity (Tanimoto coeff. over hybridization fingerprint)",
     col=color
)
abline(h=mean(moa), v=mean(struc), col=color)

par(new=TRUE)