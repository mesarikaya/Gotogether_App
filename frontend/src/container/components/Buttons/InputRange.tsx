import * as React from 'react';
import '../../../stylesheets/css/inputRange.css';

export interface Props {
  name: string;
  value: string|number;
  onChangeValue: (name: string, value: number) => Promise<void>;
  onIncrease: (name:string) => void;
  onDecrease: (name:string) => void;
};

class InputRange extends React.Component<Props> {

  constructor(props:Props){
    super(props);

    this.increase = this.increase.bind(this);
    this.decrease = this.decrease.bind(this);
    this.handleInputChange = this.handleInputChange.bind(this);
  }

  public decrease = () => {
    this.props.onDecrease(this.props.name)
  }

  public handleInputChange = async (event: any): Promise<void> => {
    const enteredRange = this.setRange(Number(event.target.value));
    this.props.onChangeValue(this.props.name, enteredRange);
  }

  public increase = () => {
    this.props.onIncrease(this.props.name);
  }
  
  public render() {

    const rangeValue = this.setRange(this.props.value).toString();

    return (
        <div className="def-number-input number-input">
          <button onClick={this.decrease} className="minus" type="button"/>
          <input onChange={this.handleInputChange}
            className="quantity" name="quantity" value={rangeValue} type="number"/>
          <button onClick={this.increase} className="plus" type="button" />
        </div>
      );
  }

  private isValidEntry(entry: string|number){
    entry = Number(entry);
    return (isNaN(entry) || entry<0 || typeof entry !== 'undefined' || entry==='' || entry === null);
  }

  private setRange(entry: string|number){
    const rangeValue = Number(entry);
    return (this.isValidEntry(rangeValue)) ? rangeValue: 0;
  }
}

export default InputRange;