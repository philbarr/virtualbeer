SET LIBGDX="C:\Development\Java\lib\libgdx-0.9.9"
java -cp %LIBGDX%\gdx.jar;%LIBGDX%\extensions\gdx-tools\gdx-tools.jar com.badlogic.gdx.tools.imagepacker.TexturePacker2 output atlas beermenu
robocopy atlas ..\..\..\..\VirtualBeer-android\assets\data\menu /LOG:robocopylog.txt
