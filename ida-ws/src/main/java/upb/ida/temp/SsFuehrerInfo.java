package upb.ida.temp;

import java.util.Date;
import java.util.TreeSet;

import org.joda.time.Interval;

public class SsFuehrerInfo {
	int count = 0;

	TreeSet<Date> promotionDates;
	int lastSsRang = -1;
	int maxRang = -1;

	public void updateData(int rang, Date promotionDate) {
		if (lastSsRang == -1) {
			lastSsRang = rang;
			maxRang = rang;
		} else {
			if (rang > maxRang) {
				maxRang = rang;
			} else if (rang < lastSsRang) {
				lastSsRang = rang;
			}
		}
		count = maxRang - lastSsRang;
		addPromotionDate(promotionDate);
	}

	public void addPromotionDate(Date promotionDate) {
		if (promotionDates == null) {
			promotionDates = new TreeSet<>();
		}
		promotionDates.add(promotionDate);
	}

	public float getRangFreq() {
		float rangFreq = 0;
		if (count > 0) {
			// Calculate
			Date start = promotionDates.first();
			Date end = promotionDates.last();
			Interval interval = new Interval(start.getTime(), end.getTime());
			Integer days = interval.toDuration().toStandardDays().getDays();
			rangFreq = (count * 365f) / days.floatValue();
		}
		return rangFreq;
	}

	public int getCount() {
		return count;
	}

	public int getLastSsRang() {
		return lastSsRang;
	}

	public TreeSet<Date> getPromotionDates() {
		return promotionDates;
	}

	@Override
	public String toString() {
		return "SsFuehrerInfo [count=" + count + ", promotionDates=" + promotionDates + ", lastSsRang=" + lastSsRang
				+ ", maxRang=" + maxRang + "]";
	}

}
