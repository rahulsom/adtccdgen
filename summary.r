#
# summary.r
#  Summarizes data collected from timing.txt
#
# usage
#  R -q --no-save < summary.r; open *.png
#
rawData <- read.csv('timing.txt', header = F)
summary(rawData)
sd(rawData[ , 1])

responseTime = as.matrix(rawData)
max_num <- max(responseTime)/100
png(filename='hist.png')
hist(responseTime, col=heat.colors(10), breaks=max_num)
dev.off()

png(filename='time.png')
plot(responseTime, type="h", col="blue")
dev.off()

ma <- function(x,n=100){filter(x,rep(1/n,n), sides=2)}
avgRespTime <- ma(responseTime)
png(filename='ma.png')
plot(avgRespTime, type="l", col="purple")
dev.off()
