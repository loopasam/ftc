# Plot of sim(moa) vs. sim(structure)
# Only compounds that are present in different categories (based on a treshold) are
# represented.

values <- read.csv("/home/samuel/git/ftc/data/analysis/diff_cats_1lvl.csv", 
                   head=TRUE, 
                   sep=","
)

moa <- values$firstSim
struc <- values$secondSim

plot(struc, 
     moa,
     pch='*',
     main='Structural similarity versus mode of action similarity\nOnly compunds in different ATC categories (one level)', 
     ylab="Mode of action similarity (Jaccard index over ancestor classes)",
     xlab="Structural similarity (Tanimoto coeff. over hybridization fingerprint)",
)

abline(h=0.5, v=0.5)