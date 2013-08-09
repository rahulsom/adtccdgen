#
# summary.r
#  Summarizes data collected from timing.txt
#
# usage
#  R -q --no-save < summary.r; open *.png
#
rawData <- read.csv('build/timing.txt', header = F)

responseTime = as.matrix(rawData)
bins <- max(log10(responseTime))*15
png(filename='build/hist.png',width=1024, height=796)
h <- hist(log10(responseTime), breaks=bins)$counts

hist(log10(responseTime), col=heat.colors(bins), breaks=bins)
text(
  round(max(log10(rawData[, 1])/1.5)), max(h)/2, 
  paste(
    "Samples =", length(rawData[, 1]), 
    "\nMean =", round(mean(rawData[, 1]),2), 
    "\nMedian =", median(rawData[, 1]), 
    "\n1% =", round(quantile(rawData[, 1], c(0.01)),2), 
    "\n5% =", round(quantile(rawData[, 1], c(0.05)),2), 
    "\n95% =", round(quantile(rawData[, 1], c(0.95)),2), 
    "\n99% =", round(quantile(rawData[, 1], c(0.99)),2), 
    "\nMin =", min(rawData[, 1]), 
    "\nMax =", max(rawData[, 1]), 
    "\nStd.Dev =", round(sd(rawData[ , 1]),2)
  ), 
  pos = 4
)
dev.off()

png(filename='build/time.png',width=1024, height=796)
plot(responseTime, type="h", col="blue")
dev.off()

ma <- function(x,n=100){filter(x,rep(1/n,n), sides=2)}
avgRespTime <- ma(responseTime)
png(filename='build/ma.png',width=1024, height=796)
plot(avgRespTime, type="l", col="purple")
dev.off()
