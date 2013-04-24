require(gplots)

#Loads the data
values <- read.csv("/home/samuel/git/ftc/data/analysis/moa_similarities.csv", head=TRUE, sep=",")
#Determines the size of the array
col_categories <- length(colnames(values))
# Retrives the last column, aka the ATC categories
categories <- values[,col_categories]
categories <- factor(categories)

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
for(i in 1:length(categories))
  categories.colors[i] <- level.colors[ categories[i]==levels(categories) ]
#Read as matrix for heatmap
values <- as.matrix(values)
class(values) <- "numeric"

#populates row names based off column names
rownames(values) <- colnames(values)
# Custom defintion of palettes
palette <- colorRampPalette(c('#FFFFFF','#000000'))(256)

# distfunction parameter change
heatmap.2(values, 
          scale='none',
          col=palette,
          tracecol=FALSE,
          ColSideColors=categories.colors,
          RowSideColors=categories.colors,
          labRow=NA,
          distfun = function(x) dist(x,method = 'manhattan'),
          xlab="DrugBank compounds",
          ylab="DrugBank compounds",
          labCol=NA,
          density.info = "none",
          main='Mode of actions pairwise similarities.\nDrugs are clustered by mode of action similarities.')