package com.rabbit.magazine.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.Attributes;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.sax.TextElementListener;
import android.util.Log;

import com.rabbit.magazine.kernel.Animation;
import com.rabbit.magazine.kernel.BasicView;
import com.rabbit.magazine.kernel.Category;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Hot;
import com.rabbit.magazine.kernel.Layer;
import com.rabbit.magazine.kernel.Magazine;
import com.rabbit.magazine.kernel.Page;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.kernel.PictureSet;
import com.rabbit.magazine.kernel.Rotater;
import com.rabbit.magazine.kernel.Shutter;
import com.rabbit.magazine.kernel.Slider;
import com.rabbit.magazine.kernel.Video;
import com.rabbit.magazine.view.FrameUtil;

/**
 * Content解析 以下XMLReader的这种解析方式也是属于sax解析， 同我们使用SaxParser功能一样。但是这种功能更加的强大.
 * 
 * @author litingwen
 * 
 */
public class MagazineReader {

	private static final String VISIBLE = "visible";

	private static final String TAG = "tag";

	private static final String LAYER = "layer";
	
	private static final String BLOCKING = "blocking";
	
	private static final String OPENINGEFFECT="openingEffect";
	
	private static final String CLOSINGEFFECT="closingEffect"; 
			
	private static final String ARGUMENT = "argument";

	private static final String ACTION = "action";

	private static final String HOT = "hot";

	private static final String INDEX = "index";

	private static final String ZOOMABLE = "zoomable";
	
	private static final String ZOOMEDIMAGE = "zoomedImage";
	
	private static final String ZOOMICON = "zoomIcon";

	private static final String SIZE = "size";

	private static final String PAGED = "paged";

	private static final String CONTENT_SIZE = "contentSize";

	private static final String ORIENTATION = "orientation";

	private static final String FRAME = "frame";

	private static final String TITLE = "title";

	private static final String GROUP = "group";

	private static final String NAME = "name";
	
	private static final String EFFECT="effect";

	private static final String IMG = "img";

	private static final String PAGE = "page";

	private static final String ROOT = "root";

	private static final String DESCRIPTION = "description";

	private static final String CATEGORY = "category";

	private static final String PICTURE = "picture";

	private static final String PICTURESET = "pictureSet";
	private static final String ENVELOPE="envelope";
	
	private static final String FRAMELENGTH="frameLength";
	
	private static final String CYCLING="cycling";
	
	private static final String DELAY="delay";
	
	private static final String CLOSEONEND="closesOnEnd";

	protected static final String STYLE = "style";

	protected static final String FULLSCREENMODE = "fullscreenMode";
	// Video
	protected static final String AUTOMATIC = "automatic";
	protected static final String FULLSCREEN = "fullscreen";
	protected static final String CLOSESONEND = "closesOnEnd";
	
	protected static final String INDICTORICONS="indicatorIcons";
	protected static final String INTERVAL="interval";
	
	protected static final String PREVIEW = "preview";
	protected static final String PLAYICON = "playIcon";

	private Magazine magazine = new Magazine();
	Category category;
	protected Page page;
	protected Group group;
	protected Group subgroup;
	protected Group thirdgroup;
	protected Group forthgroup;
	protected Group currgroup;
	protected Picture picture;
	protected PictureSet pictureset;
	protected Hot hot;
	protected Layer layer;
	protected Animation animation;
	protected Slider slider;
	protected Shutter shutter;
	protected Video video;
	protected Rotater rotater;
	
	private int iterateNum;
	
	/**
	 * 
	 * @return 返回设置好处理机制的rootElement
	 */
	public RootElement getRootElement() {
		/* rootElement代表着根节点，参数为根节点的tagName */
		RootElement rootElement = new RootElement("root");
		Element categoryElement = parseCategory(rootElement);
		Element pageElement = parsePage(categoryElement);
		List<Element> groups = new ArrayList<Element>();
		Element groupElement = parseGroup(pageElement, groups);
//		visitGroupElement(groups,groupElement);
		
		// 二级Group解析
		Element subGroupElement = groupElement.getChild("group");
		groups.add(subGroupElement);
		subGroupElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				subgroup = new Group();
				createGroup(subgroup,attributes);
				currgroup = subgroup;
			}
		});
		subGroupElement.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				group.getGroups().add(subgroup);
				currgroup = group;
			}
		});
		// 三级Group解析
		Element thirdGroupElement = subGroupElement.getChild("group");
		groups.add(thirdGroupElement);
		thirdGroupElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				thirdgroup = new Group();
				createGroup(thirdgroup,attributes);
				currgroup = thirdgroup;
			}
		});
		thirdGroupElement.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				subgroup.getGroups().add(thirdgroup);
				currgroup = subgroup;
			}
		});
		// 枚举型解析法
		// 四级Group解析
		Element forthGroupElement = thirdGroupElement.getChild("group");
		groups.add(forthGroupElement);
		forthGroupElement.setStartElementListener(new StartElementListener() {
			
			@Override
			public void start(Attributes attributes) {
				forthgroup = new Group();
				createGroup(forthgroup,attributes);
				currgroup = forthgroup;
			}
		});
		forthGroupElement.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				thirdgroup.getGroups().add(forthgroup);
				currgroup = thirdgroup;
			}
		});
		
		for (Iterator iterator = groups.iterator(); iterator.hasNext();) {
			Element element = (Element) iterator.next();
			// Picture解析
			Element pictureElement = element.getChild("picture");
			pictureElement.setStartElementListener(new StartElementListener() {

				@Override
				public void start(Attributes attributes) {
					picture = new Picture();
					String frame = attributes.getValue(FRAME);
					String orientation = attributes.getValue(ORIENTATION);
					String size = attributes.getValue(SIZE);
					String zoomable = attributes.getValue(ZOOMABLE);
					String name = attributes.getValue(NAME);
					String zoomedImage=attributes.getValue(ZOOMEDIMAGE);
					String zoomIcon=attributes.getValue(ZOOMICON);
					picture.setName(name);
					picture.setFrame(frame);
					picture.setOrientation(orientation);
					picture.setSize(size);
					picture.setZoomable(zoomable);
					picture.setZoomedImage(zoomedImage);
					picture.setZoomIcon(zoomIcon);
				}
			});
			pictureElement.setEndElementListener(new EndElementListener() {
				@Override
				public void end() {
					currgroup.getPictures().add(picture);
				}
			});
			pictureElement.setEndTextElementListener(new EndTextElementListener() {

				@Override
				public void end(String body) {
					picture.setResource(body);
				}
			});
			// Picture Set解析
			Element picturesetElement = element.getChild("pictureSet");
			picturesetElement.setStartElementListener(new StartElementListener() {

				@Override
				public void start(Attributes attributes) {
					pictureset = new PictureSet();
					String frame = attributes.getValue(FRAME);
					String index = attributes.getValue(INDEX);
					String orientation = attributes.getValue(ORIENTATION);
					pictureset.setFrame(frame);
					pictureset.setIndex(index);
					pictureset.setOrientation(orientation);
					pictureset.setEnvelope(attributes.getValue(ENVELOPE));
				}
			});
			picturesetElement.setEndElementListener(new EndElementListener() {
				@Override
				public void end() {
					currgroup.getPictureSets().add(pictureset);
				}
			});
			picturesetElement.setEndTextElementListener(new EndTextElementListener(){
				@Override
				public void end(String body) {
					String[] resources=body.split("/");
					pictureset.setResources(resources);
				}
			});
			// animation 解析
			Element animationElement = element.getChild("animation");
			animationElement.setStartElementListener(new StartElementListener() {

				@Override
				public void start(Attributes attributes) {
					animation = new Animation();
					String frame = attributes.getValue(FRAME);
					String frameLength = attributes.getValue(FRAMELENGTH);
					String cycling=attributes.getValue(CYCLING);
					String delay=attributes.getValue(DELAY);
					String closesOnEnd=attributes.getValue(CLOSEONEND);
					animation.setFrame(frame);
					animation.setFrameLength(frameLength);
					animation.setCycling(cycling);
					animation.setDelay(delay);
					animation.setClosesOnEnd(closesOnEnd);
				}
			});
			animationElement.setEndElementListener(new EndElementListener() {
				@Override
				public void end() {
					currgroup.getAnimations().add(animation);
				}
			});
			animationElement.setEndTextElementListener(new EndTextElementListener(){
				@Override
				public void end(String body) {
					String[] resources=body.split("/");
					animation.setResources(resources);
				}
			});
			
			//Slider解析
			Element sliderElement = element.getChild("slider");
			sliderElement.setStartElementListener(new StartElementListener() {
				@Override
				public void start(Attributes attributes) {
					slider=new Slider();
					String frame = attributes.getValue(FRAME);
					slider.setFrame(frame);
					String indicatorIcons=attributes.getValue(INDICTORICONS);
					slider.setSelected(indicatorIcons.split("\\|")[0]);
					slider.setUnselected(indicatorIcons.split("\\|")[1]);
				}
			});
			sliderElement.setEndElementListener(new EndElementListener() {
				@Override
				public void end() {
					currgroup.getSliders().add(slider);
				}
			});
			
			//Slider>>Picture
			Element sliderPicElement = sliderElement.getChild("picture");
			sliderPicElement.setStartElementListener(new StartElementListener() {
				@Override
				public void start(Attributes attributes) {
					picture = new Picture();
					String frame = attributes.getValue(FRAME);
					picture.setFrame(frame);
				}
			});
			sliderPicElement.setEndTextElementListener(new EndTextElementListener() {
				@Override
				public void end(String body) {
					picture.setResource(body);
				}
			});
			sliderPicElement.setEndElementListener(new EndElementListener() {
				@Override
				public void end() {
					slider.getPictures().add(picture);
				}
			});
			
			//Shutter解析
			Element shutterElement = element.getChild("shutter");
			shutterElement.setStartElementListener(new StartElementListener() {
				@Override
				public void start(Attributes attributes) {
					shutter=new Shutter();
					String frame = attributes.getValue(FRAME);
					shutter.setFrame(frame);
					String indicatorIcons=attributes.getValue(INDICTORICONS);
					shutter.setSelected(indicatorIcons.split("\\|")[0]);
					shutter.setUnselected(indicatorIcons.split("\\|")[1]);
					String interval=attributes.getValue(INTERVAL);
					shutter.setInterval(Integer.parseInt(interval));
				}
			});
			shutterElement.setEndElementListener(new EndElementListener() {
				@Override
				public void end() {
					currgroup.getShutters().add(shutter);
				}
			});
			
			//Shutter>>Picture
			Element shutterPicElement = shutterElement.getChild("picture");
			shutterPicElement.setStartElementListener(new StartElementListener() {
				@Override
				public void start(Attributes attributes) {
					picture = new Picture();
					String frame = attributes.getValue(FRAME);
					picture.setFrame(frame);
				}
			});
			shutterPicElement.setEndTextElementListener(new EndTextElementListener() {
				@Override
				public void end(String body) {
					picture.setResource(body);
				}
			});
			shutterPicElement.setEndElementListener(new EndElementListener() {
				@Override
				public void end() {
					shutter.getPictures().add(picture);
				}
			});

			// Hot 解析
			Element hotElement = element.getChild("hot");
			hotElement.setStartElementListener(new StartElementListener() {

				@Override
				public void start(Attributes attributes) {
					hot = new Hot();
					String action = attributes.getValue(ACTION);
					String argument = attributes.getValue(ARGUMENT);
					String frame = attributes.getValue(FRAME);
					String fullscreenMode = attributes.getValue(FULLSCREENMODE);
					hot.setFrame(frame);
					hot.setAction(action);
					hot.setArgument(argument);
					hot.setFullscreenMode(fullscreenMode);
				}
			});
			hotElement.setEndElementListener(new EndElementListener() {
				@Override
				public void end() {
					currgroup.getHots().add(hot);
				}
			});
			
			// Hot>>picture 解析
			Element hotPicElement=hotElement.getChild("picture");
			hotPicElement.setEndTextElementListener(new EndTextElementListener() {
				@Override
				public void end(String body) {
					hot.setPicture(body);
				}
			});
			
			// Layer 解析
			Element layerElement = element.getChild("layer");
			layerElement.setStartElementListener(new StartElementListener() {

				@Override
				public void start(Attributes attributes) {
					layer = new Layer();
					String name = attributes.getValue(NAME);
					String frame = attributes.getValue(FRAME);
					String tag = attributes.getValue(TAG);
					String visible = attributes.getValue(VISIBLE);
					String effect=attributes.getValue(EFFECT);
					if(effect!=null){
						layer.setEffect(effect);
					}
					layer.setFrame(frame);
					layer.setName(name);
					layer.setTag(tag);
					layer.setVisible(visible);
				}
			});
			layerElement.setEndElementListener(new EndElementListener() {
				@Override
				public void end() {
					currgroup.getLayers().add(layer);
				}
			});
			// Layer>>Picture 解析
			Element layerPicElement = layerElement.getChild("picture");
			layerPicElement.setStartElementListener(new StartElementListener() {

				@Override
				public void start(Attributes attributes) {
					picture = new Picture();
					String frame = attributes.getValue(FRAME);
					String orientation = attributes.getValue(ORIENTATION);
					String size = attributes.getValue(SIZE);
					String zoomable = attributes.getValue(ZOOMABLE);
					String name = attributes.getValue(NAME);
					picture.setName(name);
					picture.setFrame(frame);
					picture.setOrientation(orientation);
					picture.setSize(size);
					picture.setZoomable(zoomable);
				}
			});
			layerPicElement.setEndElementListener(new EndElementListener() {
				@Override
				public void end() {
					layer.setPicture(picture);
				}
			});
			layerPicElement.setEndTextElementListener(new EndTextElementListener() {

				@Override
				public void end(String body) {
					picture.setResource(body);
				}
			});

			// Layer>>Hot 解析
			Element layerHotElement = layerElement.getChild("hot");
			layerHotElement.setStartElementListener(new StartElementListener() {

				@Override
				public void start(Attributes attributes) {
					hot = new Hot();
					String action = attributes.getValue(ACTION);
					String argument = attributes.getValue(ARGUMENT);
					String frame = attributes.getValue(FRAME);
					hot.setFrame(frame);
					hot.setAction(action);
					hot.setArgument(argument);
				}
			});
			layerHotElement.setEndElementListener(new EndElementListener() {
				@Override
				public void end() {
					layer.setHot(hot);
				}
			});
			// Layer>>Hot>>Picture 解析
			Element layerHotPicElement=layerHotElement.getChild("picture");
			layerHotPicElement.setEndTextElementListener(new EndTextElementListener() {
				@Override
				public void end(String body) {
					hot.setPicture(body);
				}
			});
			
			// video 解析
			Element videoElement = element.getChild("video");
			videoElement .setStartElementListener(new StartElementListener() {

				@Override
				public void start(Attributes attributes) {
					video = new Video();
					String frame = attributes.getValue(FRAME);
					String orientation= attributes.getValue(ORIENTATION);
					String automatic = attributes.getValue(AUTOMATIC);
					String closesOnEnd=attributes.getValue(CLOSESONEND);
					String fullscreen=attributes.getValue(FULLSCREEN);
					String preview=attributes.getValue(PREVIEW);
					String playIcon=attributes.getValue(PLAYICON);
					video.setFrame(frame);
					video.setOrientation(orientation);
					video.setAutomatic(automatic);
					video.setClosesOnEnd(closesOnEnd);
					video.setFullscreen(fullscreen);
					video.setPreview(preview);
					video.setPlayIcon(playIcon);
				}
			});
			videoElement.setEndElementListener(new EndElementListener() {
				@Override
				public void end() {
					currgroup.getVideos().add(video);
				}
			});
			videoElement.setEndTextElementListener(new EndTextElementListener() {

				@Override
				public void end(String body) {
					video.setResource(body);
				}
				
			});
			// Rotater 解析
			Element rotaterElement = element.getChild("rotater");
			parseRotater(rotaterElement);
		}

		return rootElement;

	}

	private void parseRotater(Element rotaterElement) {
		rotaterElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				rotater = new Rotater();
				String frame = attributes.getValue(FRAME);
				String orientation = attributes.getValue(ORIENTATION);
				rotater.setFrame(frame);
				rotater.setOrientation(orientation);
			}
		});
		rotaterElement.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				currgroup.getRotaters().add(rotater);
			}
		});
		rotaterElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				rotater.setResource(body);
			}
			
		});
	}

	private Element parseGroup(Element pageElement, List<Element> groups) {
		// 一级Group 节点
		Element groupElement = pageElement.getChild("group");
		groups.add(groupElement);
		groupElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				group = new Group();
				createGroup(group,attributes);
				currgroup = group;
			}
		});
		groupElement.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				page.getGroups().add(group);
			}
		});
		return groupElement;
	}

	private Element parsePage(Element categoryElement) {
		// 页节点
		Element pageElement = categoryElement.getChild("page");
		pageElement.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				page = new Page();
				iterateNum=0;
				String description = attributes.getValue(DESCRIPTION);
				String bgColor = attributes.getValue("bgColor");
				String title = attributes.getValue(TITLE);
				page.setBgColor(bgColor);
				page.setTitle(title);
				page.setDescription(description);
			}
		});
		pageElement.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				category.getPages().add(page);
			}
		});
		// Page direct include picture 
		Element pagePicEle = pageElement.getChild("picture");
		pagePicEle.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				picture = new Picture();
				String frame = attributes.getValue(FRAME);
				picture.setFrame(frame);
			}
		});
		
		pagePicEle.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				page.getPictures().add(picture);
			}
		});
		pagePicEle.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				picture.setResource(body);
			}
		});
		// Page direct include Hot Element 
		Element pagehotEle = pageElement.getChild("hot");
		pagehotEle.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				hot = new Hot();
				String action = attributes.getValue(ACTION);
				String argument = attributes.getValue(ARGUMENT);
				String frame = attributes.getValue(FRAME);
				String fullscreenMode = attributes.getValue(FULLSCREENMODE);
				hot.setFrame(frame);
				hot.setAction(action);
				hot.setArgument(argument);
				hot.setFullscreenMode(fullscreenMode);
			}
		});
		pagehotEle.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				page.getHots().add(hot);
			}
		});
		
		// Page direct include layer Element 
		Element pagelayerElement = pageElement.getChild("layer");
		pagelayerElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				layer = new Layer();
				String name = attributes.getValue(NAME);
				String frame = attributes.getValue(FRAME);
				String tag = attributes.getValue(TAG);
				String visible = attributes.getValue(VISIBLE);
				String effect=attributes.getValue(EFFECT);
				if(effect!=null){
					layer.setEffect(effect);
				}
				layer.setFrame(frame);
				layer.setName(name);
				layer.setTag(tag);
				layer.setVisible(visible);
			}
		});
		pagelayerElement.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				page.getLayers().add(layer);
			}
		});
		// Layer>>Picture 解析
		Element pagelayerPicElement = pagelayerElement.getChild("picture");
		pagelayerPicElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				picture = new Picture();
				String frame = attributes.getValue(FRAME);
				String orientation = attributes.getValue(ORIENTATION);
				String size = attributes.getValue(SIZE);
				String zoomable = attributes.getValue(ZOOMABLE);
				String name = attributes.getValue(NAME);
				picture.setName(name);
				picture.setFrame(frame);
				picture.setOrientation(orientation);
				picture.setSize(size);
				picture.setZoomable(zoomable);
			}
		});
		pagelayerPicElement.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				layer.setPicture(picture);
			}
		});
		pagelayerPicElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				picture.setResource(body);
			}
		});

		// Layer>>Hot 解析
		Element pagelayerHotElement = pagelayerElement.getChild("hot");
		pagelayerHotElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				hot = new Hot();
				String action = attributes.getValue(ACTION);
				String argument = attributes.getValue(ARGUMENT);
				String frame = attributes.getValue(FRAME);
				hot.setFrame(frame);
				hot.setAction(action);
				hot.setArgument(argument);
			}
		});
		pagelayerHotElement.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				layer.setHot(hot);
			}
		});
		
		// Page direct include PictureSet Element 
		Element pagepicsetElement = pageElement.getChild("pictureSet");
		pagepicsetElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				pictureset = new PictureSet();
				String frame = attributes.getValue(FRAME);
				String index = attributes.getValue(INDEX);
				pictureset.setFrame(frame);
				pictureset.setIndex(index);
			}
		});
		
		pagepicsetElement.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				page.getPictureSets().add(pictureset);
			}
		});
		pagepicsetElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				String[] resources=body.split("/");
				pictureset.setResources(resources);
			}
		});
		
		// Page direct include animation Element 
		Element pageanimateElement = pageElement.getChild("animation");
		pageanimateElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {  
				animation = new Animation();
				String frame = attributes.getValue(FRAME);
				String frameLength = attributes.getValue(FRAMELENGTH);
				String cycling=attributes.getValue(CYCLING);
				String delay=attributes.getValue(DELAY);
				String closesOnEnd=attributes.getValue(CLOSEONEND);
				animation.setFrame(frame);
				animation.setFrameLength(frameLength);
				animation.setCycling(cycling);
				animation.setDelay(delay);
				animation.setClosesOnEnd(closesOnEnd);
			}
		});
		
		pageanimateElement.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				page.getAnimations().add(animation);
			}
		});
		pageanimateElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				String[] resources=body.split("/");
				animation.setResources(resources);
			}
		});
		
		// Page Internal Video 解析
		Element pvideoElement = pageElement.getChild("video");
		pvideoElement .setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				video = new Video();
				String frame = attributes.getValue(FRAME);
				String orientation= attributes.getValue(ORIENTATION);
				String automatic = attributes.getValue(AUTOMATIC);
				String closesOnEnd=attributes.getValue(CLOSESONEND);
				String fullscreen=attributes.getValue(FULLSCREEN);
				video.setFrame(frame);
				video.setOrientation(orientation);
				video.setAutomatic(automatic);
				video.setClosesOnEnd(closesOnEnd);
				video.setFullscreen(fullscreen);
			}
		});
		pvideoElement.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				page.getVideos().add(video);
			}
		});
		pvideoElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				video.setResource(body);
			}
			
		});
		return pageElement;
	}

	private Element parseCategory(RootElement rootElement) {
		// Category 节点
		Element categoryElement = rootElement.getChild("category");
		categoryElement.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				category = new Category();
				String description = attributes.getValue(DESCRIPTION);
				String image = attributes.getValue(IMG);
				String title = attributes.getValue(TITLE);
				category.setTitle(title);
				category.setDescription(description);
				category.setImage(image);
			}
		});
		categoryElement.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				getMagazine().getCategorys().add(category);
			}
		});
		return categoryElement;
	}

	private void visitGroupElement(List<Element> groups, Element groupElement) {
		try{
			Element subGroupElement = groupElement.getChild("group");
			if(iterateNum<5){
				iterateNum+=1;
				groups.add(subGroupElement);
				subGroupElement.setStartElementListener(new StartElementListener() {
		
					@Override
					public void start(Attributes attributes) {
						String frame = attributes.getValue(FRAME);
						if(frame==null||frame.equals("")){
							Log.d("", "终止解析Group");
							return;
						}else {
							subgroup=new Group();
							String orientation = attributes.getValue(ORIENTATION);
							String contentSize = attributes.getValue(CONTENT_SIZE);
							String paged = attributes.getValue(PAGED);
							subgroup.setFrame(frame);
							subgroup.setContentSize(contentSize);
							subgroup.setPaged(paged);
							subgroup.setOrientation(orientation);
						}
					}
				});
				subGroupElement.setEndElementListener(new EndElementListener() {
					@Override
					public void end() {
						currgroup.getGroups().add(subgroup);
						currgroup = subgroup;
					}
				});
				visitGroupElement(groups, subGroupElement);
			}
		}catch(Exception e){
			Log.d("", e.getMessage());
			return;
		}
	}

	public void setMagazine(Magazine magazine) {
		this.magazine = magazine;
	}

	public Magazine getMagazine() {
		return magazine;
	}

	private void createGroup(Group group, Attributes attributes) {
		String frame = attributes.getValue(FRAME);
		String orientation = attributes.getValue(ORIENTATION);
		String contentSize = attributes.getValue(CONTENT_SIZE);
		String paged = attributes.getValue(PAGED);
		String style = attributes.getValue(STYLE);
		int[] frame2int = FrameUtil.frame2int(frame);
		// 根据坐标和内容大小自动计算的模式
		if(frame!=null&&contentSize!=null){
			int[] content2Int = FrameUtil.content2Int(contentSize);
			if(frame2int[2]<content2Int[0]){
				group.setOrientation(BasicView.HORIZONTAL);
			}
		}else {
			// 指定模式
			group.setOrientation(orientation);
		}
		group.setFrame(frame);
		group.setContentSize(contentSize);
		group.setPaged(paged);
		group.setStyle(style);
	}
}
