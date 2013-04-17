values <- read.csv("/home/samuel/git/ftc/data/analysis/struct_moa_sim.csv", head=TRUE, sep=",")
palette <- colorRampPalette(c('#f0f3ff','#0033BB'))
smoothScatter(values, 
              colramp=palette,
              main='sim(structure) vs sim(MoA)')
abline(h=0.8, v=0.8)

struc <- values$structureSimilarity
moa <- values$moaSimilarity
size <- length(struc)
new_moa <- {}
new_struc <- {}
cutoff_moa_sup = 1.0
cutoff_moa_inf = 0.95
cutoff_struc_sup = 0.5
cutoff_struc_inf = 0.0

for(i in 1:size) {
  if(moa[i] >= cutoff_moa_inf && moa[i] <= cutoff_moa_sup && struc[i] >= cutoff_struc_inf && cutoff_struc_sup >= struc[i]) {
    new_moa <- c(new_moa, moa[i])
    new_struc <- c(new_struc, struc[i])
  }
}

smoothScatter(new_struc,new_moa, colramp=palette, main='sim(structure) vs sim(MoA) - focus')

for(i in 1:size) {
  if(struc[i] > 0.8 && moa[i] > 0.8){
    new_moa <- c(new_moa, moa[i])
    new_struc <- c(new_struc, struc[i])
  }
}
