#Parameters
#input: input dataset (use complete path if you are on a different directory)
#output: output density graph (specify a path if you want to save it on a different directory)

#Steps
#1. load ggplot library
#2. read dataset
#3. create a frame
#4. delete latitude and longitude columns if they exist
#5. rename columns (for graph legend)
#6. stack up the values
#7. rename the stack column corresponding to legend
#8. plot the values (and scale them in order to have a better visualization)
#9. save the graph

plot_data <- function(input, output) {
  (library(ggplot2))
  src <- read.csv(input)
  df <- as.data.frame(src)
  if(("Latitude" %in% names(df)) && ("Longitude" %in% names(df))) {
    df <- subset(df, select = -c(Latitude, Longitude))
  }
  names(df)[1]<-"pdf(Arts & Entertainment)"
  names(df)[2]<-"pdf(College & University)"
  names(df)[3]<-"pdf(Event)"
  names(df)[4]<-"pdf(Food)"
  names(df)[5]<-"pdf(Nightlife Spot)"
  names(df)[6]<-"pdf(Outdoors & Recreation)"
  names(df)[7]<-"pdf(Professional & Other Places)"
  names(df)[8]<-"pdf(Residence)"
  names(df)[9]<-"pdf(Shop & Service)"
  names(df)[10]<-"pdf(Travel & Transport)"
  dfs <- stack(df)
  names(dfs)[2]<-"legend"
  p <- ggplot(dfs, aes(x=values)) + geom_density(aes(group=legend, colour=legend, fill=legend), alpha=0.3)
  p + xlab("cells") + scale_x_log10()
  ggsave(file=output, dpi=72)
}
