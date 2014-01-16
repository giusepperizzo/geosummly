plot_data <-
function(name) {
  (library(ggplot2))
  df <- as.data.frame(name)
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

}
