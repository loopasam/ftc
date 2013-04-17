values <- read.csv("/home/samuel/git/ftc/data/analysis/atc_moa_sim.csv", head=TRUE, sep=",")
palette <- colorRampPalette(c('#f0f3ff','#0033BB'))
smoothScatter(values, colramp=palette)
abline(h=0.5, v=0.5)

ms.2circ <- function(r=1, adj=pi/2, col1='orange', col2='red', npts=180) {
  tmp1 <- seq(0,   pi, length.out=npts+1) + adj
  tmp2 <- seq(pi, 2*pi, length.out=npts+1) + adj
  polygon(cos(tmp1)*r,sin(tmp1)*r, border=NA, col=col1) 
  polygon(cos(tmp2)*r,sin(tmp2)*r, border=NA, col=col2)
  invisible(NULL)
}

my.symbols(x,y, ms.2circ, inches=0.5, add=FALSE, symb.plots=TRUE, col1="#00ff0088", col2="#ff00ff88")