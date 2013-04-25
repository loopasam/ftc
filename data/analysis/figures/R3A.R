# Similarities of MoA versus similaritites of structure - simple plot.
# All compounds are compared and plotted.
# some compunds have a very similar mode of action yet they are looking really different
# Some compounds have very similar structure yet different MoA.

values <- read.csv("/home/samuel/git/ftc/data/analysis/struct_moa_sim.csv", head=TRUE, sep=",")

moa <- values$firstSim
struc <- values$secondSim

plot(struc,
     moa,
     pch="*",
     ylab="Mode of action similarity (Jaccard index over ancestor classes)",
     xlab="Structural similarity (Tanimoto coeff. over hybridization fingerprint)",
     main="Structural similarity versus mode of action similarity"
)
abline(h=0.8, v=0.8)
