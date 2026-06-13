<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.12.1" name="objects" tilewidth="96" tileheight="128" tilecount="4" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <properties>
  <property name="atlasAsset" value="OBJECTS"/>
 </properties>
 <tile id="5" type="GameObject">
  <properties>
   <property name="animation" value="IDLE"/>
   <property name="animationSpeed" type="float" value="1"/>
   <property name="atlasAsset" value="OBJECTS"/>
   <property name="speed" type="float" value="4"/>
  </properties>
  <image source="objects/lucky.png" width="21" height="21"/>
  <objectgroup draworder="index" id="3">
   <object id="2" x="6.09906" y="16.4782" width="9.5231" height="4.70805">
    <ellipse/>
   </object>
  </objectgroup>
 </tile>
 <tile id="6" type="Prop">
  <properties>
   <property name="atlasAsset" value="OBJECTS"/>
  </properties>
  <image source="objects/tree.png" width="64" height="80"/>
 </tile>
 <tile id="7" type="Prop">
  <properties>
   <property name="atlasAsset" value="OBJECTS"/>
  </properties>
  <image source="objects/houseBig.png" width="96" height="128"/>
 </tile>
 <tile id="8" type="Prop">
  <properties>
   <property name="atlasAsset" value="OBJECTS"/>
  </properties>
  <image source="objects/horse.png" width="60" height="33"/>
 </tile>
</tileset>
