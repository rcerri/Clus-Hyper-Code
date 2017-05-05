datasets <- c('oes10','oes97','rf1','rf2','scm1d','scm20d','water-quality')
numFolds <- 9
numJobs <- 4

arq <- "summary.regression.txt"
sink(arq)

for(d in 1:length(datasets)){
	cat("\n===========================================\n")
	for(i in 0:numFolds){
		for(j in 0:numJobs){
			arquivo <- paste(datasets[d],"/Fold",i,"/job.",j,".out.stat",sep="")
			try({
				conteudo <- scan(arquivo,what="character",sep="\n")
				melhor <- conteudo[(length(conteudo)-4):length(conteudo)]
				cat(arquivo,"\n")
				for(l in 1:length(melhor)){
					cat(melhor[l],"\n")
				}
			})
		}
	}
	try({
		medias.final <- paste(datasets[d],"/resultadoAllFinal.csv",sep="")
		medias <- scan(medias.final,what="character",sep="\n")
		cat("Mean all:\n")
		print(medias)
	})
}

sink()
close(arq)
