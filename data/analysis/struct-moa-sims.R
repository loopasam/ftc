values <- read.csv("/home/samuel/git/ftc/data/analysis/struct_moa_sim.csv", head=TRUE, sep=",")
palette <- colorRampPalette(c('#ffffff','#ff0000'))
smoothScatter(values, colramp=palette)
abline(h=0.5, v=0.5)

struc <- values$structureSimilarity
moa <- values$moaSimilarity
size <- length(struc)
new_moa <- {}
new_struc <- {}

for(i in 1:size) {
  if(moa[i] > 0.8){
    new_moa <- c(new_moa, moa[i])
    new_struc <- c(new_struc, struc[i])
  }
}

smoothScatter(new_struc,new_moa, colramp=palette)

for(i in 1:size) {
  if(struc[i] > 0.8){
    new_moa <- c(new_moa, moa[i])
    new_struc <- c(new_struc, struc[i])
  }
}

