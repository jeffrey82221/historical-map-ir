library(leaflet)

df_history <- read.csv("emap_A.csv")
df_history <- df_history[,c("name_tw","lat","lng","size_check","owner","years","website","address")]
df_history[,"size_check"] <- df_history[,"size_check"]/64

df_history[,"years"] <- as.numeric(gsub("([0-9]+).*$", "\\1", df_history[,"years"]))
df_history[which(is.na(df_history[,"years"])),"years"] <- 2000

m = leaflet(df_history) %>% addTiles()

owner <- unique(df_history[,"owner"])
RdYlBu = colorFactor("Spectral", owner)

m %>% addCircleMarkers(~lng, ~lat, radius = ~size_check,
                       color = ~RdYlBu(owner), fillOpacity = 0.5)

