# Heat map of DrugBank compounds compared based on their MoA similarities.
# Compounds are organised by disease categories (one level resolution).
# This plot shows that there is indeed clusters: Compounds in the same therapeutical category (ATC)
# tends to have similar MoA.

library(gplots)

# Plotting orderted by ATC colors
values <- read.csv("/home/samuel/git/ftc/data/analysis/moa_similarities.csv", head=TRUE, sep=",")
bu <- values
col_categories <- length(colnames(values))
# Get the names of categories from the last column
names <- values[,c(col_categories)]
# re-order the matrix based on the ATC categories
values <- values[order(values[,col_categories]),]
# Keep the ordered categories for plotting later
sorted_names <- values[,c(col_categories)]
# Removes the categories col
values <- values[,-c(col_categories)]
# Add it to the matrix
values <- rbind(values, as.character(names))
# order the matrix based on categories. Step important in order for the matrix to be well done
values <- values[,order(values[col_categories,])]
# removes the last column
values <- values[-c(col_categories),]
# Add the sorted names for rest of the script (dirty)
values <- cbind(values, as.character(sorted_names))

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
          density.info="none",
          ColSideColors=categories.colors,
          RowSideColors=categories.colors,
          labRow=FALSE,
          labCol=FALSE,
          dendrogram = "none",
          xlab="DrugBank compounds",
          ylab="DrugBank compounds",
          Rowv=NA,
          Colv=NA,
          main='Mode of actions pairwise similarities.\nDrugs are ordered by ATC categories.')
