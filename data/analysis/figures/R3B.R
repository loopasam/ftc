# Script to zoom in a particular part of the Moa vs structure plot
# The ATC categories of the compunds are appearing.
# So basically: Compounds with different categories and highly similar MoA are hypothesis too.
# Looking also at compunds that have similar structure yet different MoA
# All these compounds are selected because they are present in very different ATC categories
# (one level - tunable from file name).

values <- read.csv("/home/samuel/git/ftc/data/analysis/diff_cats_2lvl.csv", 
                   head=TRUE, 
                   sep=","
)

moa <- values$firstSim
struc <- values$secondSim
id1 <- as.vector(values$id1)
id2 <- as.vector(values$id2)
atc1 <- as.vector(values$atc1)
atc2 <- as.vector(values$atc2)

size <- length(struc)
new_moa <- {}
new_struc <- {}
new_atc1 <- {}
new_atc2 <- {}
new_id1 <- {}
new_id2 <- {}
cutoff_struc_sup = 1.0
cutoff_struc_inf = 0.0
cutoff_moa_sup = 1.0
cutoff_moa_inf = 0.8

for(i in 1:size) {
  if(moa[i] >= cutoff_moa_inf && moa[i] <= cutoff_moa_sup && struc[i] >= cutoff_struc_inf && cutoff_struc_sup >= struc[i]) {
    new_moa <- c(new_moa, moa[i])
    new_struc <- c(new_struc, struc[i])
    new_atc1 <- c(new_atc1, atc1[i])
    new_atc2 <- c(new_atc2, atc2[i])
    new_id1 <- c(new_id1, id1[i])
    new_id2 <- c(new_id2, id2[i])
  }
}

level.colors <- rainbow(nlevels(values$atc1),start=0,end=0.9)
# Manual assignement of colors for special categories
for(i in 1:nlevels(values$atc1)) {
  cat <- levels(values$atc1)[i]
  if(cat == "Multiple") {
    level.colors[i] <- "#000000"
  } else if(cat == "NoCategory") {
    level.colors[i] <- "#FFFFFF"
  }
}

colors.atc1 <- rep(0,length(new_atc1))
colors.atc2 <- rep(0,length(new_atc2))

for(i in 1:length(new_atc1)) {
  colors.atc1[i] <- level.colors[ new_atc1[i]==levels(values$atc1) ]
  colors.atc2[i] <- level.colors[ new_atc2[i]==levels(values$atc2) ]
}

library(TeachingDemos)
ms.2circ <- function(r=0.5, adj=pi/2, col1='orange', col2='red', npts=180) {
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
  my.symbols(new_struc[i], new_moa[i], ms.2circ, inches=0.2, add=TRUE, symb.plots=TRUE, col1=colors.atc1[i], col2=colors.atc2[i])
}

library(calibrate)
label <- {}
for(i in 1:length(new_id1)) {
  text <- paste(new_id1[i], "/",new_id2[i])
  label <- c(label, text)
}

textxy(new_struc, new_moa, label, cx=0.7)