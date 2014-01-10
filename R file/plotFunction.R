plot_data <-
function(name){
(library(ggplot2))
df <- as.data.frame(name)
df <- subset(df, select = -c(Latitude, Longitude))
dfs <- stack(df)
p <- ggplot(dfs, aes(x=values)) + geom_density(aes(group=ind, colour=ind, fill=ind), alpha=0.3)
p + scale_x_log10()
}
