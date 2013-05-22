colors = c('#0000ff', '#ffab00', '#00ff18', '#ff0091')
text = c('one level', 'two levels', 'three levels', 'four levels')

# Legend hack - to finish in Inkscape
plot(c(0,0), col="white")
legend("bottomleft",legend=text, fill=colors, title="ATC levels")