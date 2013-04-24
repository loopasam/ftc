values <- read.csv("/home/samuel/git/ftc/data/analysis/struct_moa_sim.csv", head=TRUE, sep=",")

moa <- values$firstSim
struc <- values$secondSim
cat1 <- as.vector(values$id1)
cat2 <- as.vector(values$id2)

size <- length(struc)
new_moa <- {}
new_struc <- {}
new_cat1 <- {}
new_cat2 <- {}
cutoff_struc_sup = 0.2
cutoff_struc_inf = 0.0
cutoff_moa_sup = 0.05
cutoff_moa_inf = 0.0


for(i in 1:size) {
  if(moa[i] >= cutoff_moa_inf && moa[i] <= cutoff_moa_sup && struc[i] >= cutoff_struc_inf && cutoff_struc_sup >= struc[i]) {
    new_moa <- c(new_moa, moa[i])
    new_struc <- c(new_struc, struc[i])
    new_cat1 <- c(new_cat1, cat1[i])
    new_cat2 <- c(new_cat2, cat2[i])
  }
}

level.colors <- rainbow(nlevels(values$id1),start=0,end=0.9)
# Manual assignement of colors for special categories
for(i in 1:nlevels(values$id1)) {
  cat <- levels(values$id1)[i]
  if(cat == "Multiple") {
    level.colors[i] <- "#000000"
  } else if(cat == "NoCategory") {
    level.colors[i] <- "#FFFFFF"
  }
}

colors.cat1 <- rep(0,length(new_cat1))
colors.cat2 <- rep(0,length(new_cat2))

for(i in 1:length(new_cat1)) {
  colors.cat1[i] <- level.colors[ new_cat1[i]==levels(values$id1) ]
  colors.cat2[i] <- level.colors[ new_cat2[i]==levels(values$id1) ]
}

library(TeachingDemos)
ms.2circ <- function(r=1, adj=pi/2, col1='orange', col2='red', npts=180) {
  tmp1 <- seq(0,   pi, length.out=npts+1) + adj
  tmp2 <- seq(pi, 2*pi, length.out=npts+1) + adj
  polygon(cos(tmp1)*r,sin(tmp1)*r, border=NA, col=col1) 
  polygon(cos(tmp2)*r,sin(tmp2)*r, border=NA, col=col2)
  invisible(NULL)
}

plot(c(0), 
     c(0), 
     type="n", 
     main='Structural similarity versus mode of action similarity', 
     ylab="Mode of action similarity (Jaccard index over ancestor classes)",
     xlab="Structural similarity (Tanimoto coeff. over hybridization fingerprint)",
     xlim=c(cutoff_struc_inf,cutoff_struc_sup),
     ylim=c(cutoff_moa_inf,cutoff_moa_sup)
)

for(i in 1:length(new_struc)) {
  my.symbols(new_struc[i], new_moa[i], ms.2circ, inches=0.2, add=TRUE, symb.plots=TRUE, col1=colors.cat1[i], col2=colors.cat2[i])
}