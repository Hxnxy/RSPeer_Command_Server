/*
Copyright 2012 James Edwards

This file is part of Jhrome.

Jhrome is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Jhrome is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Jhrome.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sexydock.tabs.jhrome;

import org.sexydock.tabs.Utils;

import java.awt.*;

public class JhromeTabBorderAttributes
{
	public JhromeTabBorderAttributes( )
	{
	}

	public JhromeTabBorderAttributes( JhromeTabBorderAttributes other )
	{
		copyAttributes( other );
	}

	@Override
	public JhromeTabBorderAttributes clone( )
	{
		return new JhromeTabBorderAttributes( this );
	}

	public static final JhromeTabBorderAttributes	SELECTED_BORDER		= new JhromeTabBorderAttributes( );
	public static final JhromeTabBorderAttributes	UNSELECTED_BORDER;
	static
	{
		UNSELECTED_BORDER = new JhromeTabBorderAttributes( );
		UNSELECTED_BORDER.topColor = UNSELECTED_BORDER.bottomColor = new Color(80,80, 80);
		UNSELECTED_BORDER.outlineColor = new Color( 74 , 74 , 74);
		UNSELECTED_BORDER.topShadowVisible = false;
	}

	public static final JhromeTabBorderAttributes	UNSELECTED_ROLLOVER_BORDER;
	static
	{
		UNSELECTED_ROLLOVER_BORDER = new JhromeTabBorderAttributes( );
		UNSELECTED_ROLLOVER_BORDER.topColor = UNSELECTED_ROLLOVER_BORDER.bottomColor = new Color( 76, 76, 76 );
		UNSELECTED_ROLLOVER_BORDER.outlineColor = new Color( 70, 63, 71 );
		UNSELECTED_ROLLOVER_BORDER.topShadowVisible = false;
	}

	public final Insets insets				= new Insets( 1 , 15 , 0 , 15 );

	public boolean									topShadowVisible	= true;
	public Stroke shadowStroke		= new BasicStroke( 2.5f );
	public Color shadowColor			= new Color( 55 , 55 , 55 , 48 );

	public boolean	outlineVisible		= true;
	public Stroke outlineStroke		= new BasicStroke( 1f );
	public Color outlineColor		= new Color(70, 63, 71);

	public Color topColor			= new Color(81, 81, 81);
	public Color bottomColor			= new Color(97, 97, 97);

	public void copyAttributes( JhromeTabBorderAttributes other )
	{
		insets.set( other.insets.top , other.insets.left , other.insets.bottom , other.insets.right );

		topShadowVisible = other.topShadowVisible;
		shadowStroke = other.shadowStroke;
		shadowColor = other.shadowColor;

		outlineVisible = other.outlineVisible;
		outlineStroke = other.outlineStroke;
		outlineColor = other.outlineColor;

		topColor = other.topColor;
		bottomColor = other.bottomColor;
	}

	public void interpolateColors( JhromeTabBorderAttributes a , JhromeTabBorderAttributes b , float f )
	{
		shadowColor = Utils.interpolate( a.shadowColor , b.shadowColor , f );
		outlineColor = Utils.interpolate( a.outlineColor , b.outlineColor , f );
		topColor = Utils.interpolate( a.topColor , b.topColor , f );
		bottomColor = Utils.interpolate( a.bottomColor , b.bottomColor , f );
	}

}
