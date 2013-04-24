values <- read.csv("/home/samuel/git/ftc/data/analysis/struct_moa_sim.csv", head=TRUE, sep=",")

moa <- values$firstSim
struc <- values$secondSim
size <- length(struc)
cat1 <- as.vector(values$id1)
cat2 <- as.vector(values$id2)
new_moa <- {}
new_struc <- {}
new_cat1 <- {}
new_cat2 <- {}

# If equal categories are equal - saved and will be plotted
for(i in 1:size) {
  if(cat1[i] != "Multiple" && cat1[i] != "NoCategory" && cat2[i] != "Multiple" && cat2[i] != "NoCategory") {
    if(cat1[i] != cat2[i]) {
      new_moa <- c(new_moa, moa[i])
      new_struc <- c(new_struc, struc[i])
      new_cat1 <- c(new_cat1, cat1[i])
      new_cat2 <- c(new_cat2, cat2[i])
    }
  }
}

plot(new_struc,
     new_moa,
     pch="*",
     ylab="Mode of action similarity (Jaccard index over ancestor classes)",
     xlab="Structural similarity (Tanimoto coeff. over hybridization fingerprint)",
     main="Structural similarity versus mode of action similarity\nOnly compounds belonging to identical categories are shown"
)