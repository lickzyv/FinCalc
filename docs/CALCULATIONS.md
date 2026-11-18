# Calculations and assumptions

All amounts use `Double` for a small interactive estimate and are formatted for display at the edge of the app. These results are educational estimates, not bank quotes or professional tax advice.

## Interest

Time is converted to years (`months / 12`). Simple interest uses `I = P × r × t` and `A = P + I`. Compound interest uses `A = P × (1 + r / n)^(n × t)`, where `n` is 1, 2, 4, 12 or 365 times per year. `r` is the annual rate as a decimal in the formula.

## EMI

For principal `P`, monthly rate `i = annual rate / 1200` and `m` monthly payments:

- `EMI = P × i × (1 + i)^m / ((1 + i)^m − 1)`
- At zero interest, `EMI = P / m`.
- Total payment is `EMI × m`; total interest is total payment minus principal.

Tenure must resolve to whole monthly instalments. Actual lender fees, rate resets, rounding conventions and payment timing are not modeled.

## Percentage and discount

- `X% of Y = X × Y / 100`
- `X is what percent of Y = X / Y × 100` (Y must not be zero)
- Increase: `X × (1 + Y / 100)`
- Decrease: `X × (1 − Y / 100)`
- Discount amount: `original price × discount % / 100`; final price is original minus discount.

## Indian individual tax estimate — Tax Year 2026–27

### Applicable rules and terminology

The Income-tax Act, 2025 took effect from 1 April 2026 and uses **Tax Year 2026–27** for income earned during FY 2026–27. The user's familiar FY 2026–27 / AY 2027–28 wording is included in the app as a legacy reference. The 2026 Finance Act schedule and Income Tax Department FAQs provide the rates and reliefs used here. Rules are kept in `TaxRules2026.kt` so a later tax-year update has a single configuration point.

### Slabs configured

The estimator uses annual taxable income after deductions and the standard old-regime slabs for individuals under 60.

**Old regime**

| Taxable income band | Rate |
| --- | ---: |
| Up to ₹2.5 lakh | 0% |
| ₹2.5–5 lakh | 5% |
| ₹5–10 lakh | 20% |
| Above ₹10 lakh | 30% |

**New regime**

| Taxable income band | Rate |
| --- | ---: |
| Up to ₹4 lakh | 0% |
| ₹4–8 lakh | 5% |
| ₹8–12 lakh | 10% |
| ₹12–16 lakh | 15% |
| ₹16–20 lakh | 20% |
| ₹20–24 lakh | 25% |
| Above ₹24 lakh | 30% |

Slabs are marginal: each rate applies only to the income in that band.

### Rebates, surcharge and cess

- Old regime: for this resident individual estimate, rebate is up to ₹12,500 where taxable income does not exceed ₹5 lakh.
- New regime: rebate is up to ₹60,000 where taxable income does not exceed ₹12 lakh. Just above ₹12 lakh, marginal relief limits the slab tax to the income above ₹12 lakh until ordinary slab tax catches up. The official FAQ illustrates ₹12.10 lakh income with ₹10,000 tax before cess.
- Surcharge for ordinary income follows the configured thresholds: 10% above ₹50 lakh, 15% above ₹1 crore, 25% above ₹2 crore, and under the old regime 37% above ₹5 crore. The new regime caps ordinary-income surcharge at 25%. Marginal relief is applied at the surcharge thresholds.
- Health and education cess is 4% of tax after rebate/relief and surcharge.

### Scope limits

This is an estimate for a resident individual under 60 with ordinary income only. Input the taxable income after deductions; the app does not derive gross salary, standard deductions, exemptions or other deductions. It does not compute special-rate capital gains/dividends, agricultural income, senior-citizen old-regime slabs, foreign tax credits, AMT, interest, penalties, advance tax, or return-specific rounding and eligibility conditions. The displayed estimate is not actual assessed tax liability. Verify the law and your personal facts before filing.

### Official sources

- [Income Tax Department — Budget 2026 FAQ, rates and marginal relief examples](https://www.incometaxindia.gov.in/documents/20117/15766092/FAQs-Budget-2026%2BUpdated.pdf/daf54d14-aca9-c4ea-b786-598fd2f8d4c4?t=1771846962606)
- [Income Tax Department — rebate under both regimes](https://www.incometax.gov.in/iec/foportal/help/new-tax-vs-old-tax-regime-faqs)
- [India Budget — Finance Act memorandum, tax-year rates and surcharge](https://www.indiabudget.gov.in/doc/memo.pdf)
- [Income Tax Department — new Act and Tax Year transition FAQs](https://www.incometax.gov.in/iec/foportal/help/all-topics/e-filing-services/objective-and-scope-new-act-faq)

The relevant values were checked for this project on 19 November 2026. This source list is for transparency; subsequent notifications or amendments may change the rules.
