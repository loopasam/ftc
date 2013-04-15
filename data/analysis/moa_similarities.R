library(gplots)

#Loads the data
values <- read.csv("/home/samuel/git/ftc/data/analysis/moa_similarities.csv", head=TRUE, sep=",")
#Determines the size of the array
col_categories <- length(colnames(values))
# Retrives the last column, aka the ATC categories
categories <- values[,col_categories]
#Removes the column with the ATC categories
values<-values[,-c(col_categories)]
#Converts a factor (vector with levels) in a vector with colours based on the levels.
level.colors <- rainbow(nlevels(categories),start=0,end=0.9)
# Manual assignement of colors for special categories
for(i in 1:nlevels(categories)) {
  cat <- levels(categories)[i]
  if(cat == "Multiple") {
    level.colors[i] <- "#000000"
  } else if(cat == "NoCategory") {
    level.colors[i] <- "white"
  }
}
#Creates an empty array to put the colours in
categories.colors <- rep(0,length(categories))
#Fill the array with the color numbers
#From http://chromium.liacs.nl/R_users/20060207/Renee_graphs_and_others.pdf
for(i in 1:length(categories))
  categories.colors[i] <- level.colors[ categories[i]==levels(categories) ]
#Read as matrix for heatmap
values <- as.matrix(values)
#populates row names based off column names
rownames(values) <- colnames(values)
#Plot the heatmap - http://hosho.ees.hokudai.ac.jp/~kubo/Rdoc/library/gplots/html/heatmap.2.html
heatmap.2(values, scale='none', col=palette, tracecol=FALSE, density.info="none", ColSideColors=categories.colors, RowSideColors=categories.colors, labRow=FALSE, labCol=FALSE)

# distfunction parameter change
# heatmap.2(values, scale='none', col=palette, tracecol=FALSE, density.info="none", ColSideColors=categories.colors, RowSideColors=categories.colors, labRow=FALSE, labCol=FALSE, distfun = function(x) dist(x,method = 'canberra'))


# Legend hack - to finish in Inkscape
# plot(c(0,0), col="white");legend("bottomleft",legend=levels(categories), fill=level.colors,title="ATC Categories")
# Normal legend
# legend(100, 50 ,legend=levels(categories), fill=level.colors,title="ATC Categories")


# Not used yet, useful to zoom on clusters
hc.rows <- hclust(dist(values))
hc.cols <- hclust(dist(t(values)))
# heatmap_2(values[cutree(hc.rows,k=3)==1,cutree(hc.cols,k=3)==1], scale='none', col=palette, legend=2)

# Custom defintion of palettes
palette <- colorRampPalette(c('#f0f3ff','#0033BB'))(256)