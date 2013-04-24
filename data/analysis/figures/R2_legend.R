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

# Legend hack - to finish in Inkscape
plot(c(0,0), col="white")
legend("bottomleft",legend=levels(categories), fill=level.colors,title="ATC Categories")